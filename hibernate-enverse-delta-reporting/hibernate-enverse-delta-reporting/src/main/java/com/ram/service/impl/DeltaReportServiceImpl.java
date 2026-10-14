
package com.ram.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ram.model.DeltaReport;
import com.ram.repository.DeltaReportRepository;
import com.ram.service.DeltaReportService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeltaReportServiceImpl implements DeltaReportService {

	@PersistenceContext
	private EntityManager entityManager;

	private final DeltaReportRepository deltaReportRepository;

	@Override
	@Transactional
	public void generateAndSaveDeltaReport() {

		// Clear existing reports before regenerating.
		deltaReportRepository.deleteAll();

		// Process the CustomerAccount audit table.
		processAuditTable("CUSTOMER_ACCOUNT_AUD", "CustomerAccount");

		// Add more audited tables here in the future.
		// processAuditTable("ANOTHER_TABLE_AUD", "AnotherEntity");
	}

	private void processAuditTable(String auditTable, String entityName) {

		String sql = """
				SELECT *
				FROM %s
				ORDER BY REV ASC, ACCOUNT_ID ASC
				""".formatted(auditTable);

		NativeQuery<Map<String, Object>> query = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);

		query.setTupleTransformer((tuple, aliases) -> {

			Map<String, Object> row = new LinkedHashMap<>();

			for (int i = 0; i < aliases.length; i++) {
				row.put(aliases[i].toUpperCase(Locale.ROOT), tuple[i]);
			}

			return row;
		});

		List<Map<String, Object>> rows = query.getResultList();

		List<DeltaReport> reports = new ArrayList<>();

		// Store the previous audit row separately for each account.
		Map<Long, Map<String, Object>> previousRowsByAccount = new HashMap<>();

		// Cache revision timestamps.
		Map<Long, LocalDateTime> revisionTimestamps = new HashMap<>();

		LocalDateTime reportCreatedAt = LocalDateTime.now();

		for (Map<String, Object> currentRow : rows) {

			Long accountId = ((Number) currentRow.get("ACCOUNT_ID")).longValue();

			Long rev = ((Number) currentRow.get("REV")).longValue();

			Integer revType = ((Number) currentRow.get("REVTYPE")).intValue();

			Map<String, Object> previousRow = previousRowsByAccount.get(accountId);

			LocalDateTime dmlTstamp = revisionTimestamps.computeIfAbsent(rev, this::getRevisionTimestamp);

			reports.addAll(buildDeltaRows(currentRow, previousRow, accountId, rev, revType, auditTable, entityName,
					dmlTstamp, reportCreatedAt));

			// Keep track of the previous revision for this account.
			if (revType == 2) {
				previousRowsByAccount.remove(accountId);
			} else {
				previousRowsByAccount.put(accountId, currentRow);
			}
		}

		deltaReportRepository.saveAll(reports);
	}

	private List<DeltaReport> buildDeltaRows(Map<String, Object> currentRow, Map<String, Object> previousRow,
			Long accountId, Long rev, Integer revType, String auditTable, String entityName, LocalDateTime dmlTstamp,
			LocalDateTime reportCreatedAt) {

		List<DeltaReport> reports = new ArrayList<>();

		for (String column : currentRow.keySet()) {

			// Ignore Envers metadata columns.
			if ("REV".equals(column) || "REVTYPE".equals(column) || "ACCOUNT_ID".equals(column)) {
				continue;
			}

			// Ignore the modified-flag columns themselves.
			if (column.endsWith("_MOD")) {
				continue;
			}

			Object oldValue = null;
			Object newValue = null;

			switch (revType) {

			case 0 -> {
				// INSERT: old value is null.
				newValue = currentRow.get(column);
			}

			case 1 -> {
				// UPDATE: only include columns marked as modified.
				String modifiedFlagColumn = column + "_MOD";

				if (!currentRow.containsKey(modifiedFlagColumn) || !isModified(currentRow.get(modifiedFlagColumn))) {
					continue;
				}

				oldValue = previousRow == null ? null : previousRow.get(column);

				newValue = currentRow.get(column);
			}

			case 2 -> {
				// DELETE: current audit row contains the last state.
				oldValue = currentRow.get(column);
			}

			default -> {
				continue;
			}
			}

			Object accountNumber = currentRow.get("ACCOUNT_NUMBER");

			// ACCOUNT_NUMBER has modified flags disabled in our entity.
			// On deletion, use the previous state if necessary.
			if (accountNumber == null && previousRow != null) {
				accountNumber = previousRow.get("ACCOUNT_NUMBER");
			}

			reports.add(DeltaReport.builder().accountId(accountId)
					.accountNumber(accountNumber == null ? null : accountNumber.toString()).rev(rev).revType(revType)
					.modeOfOperation(getAction(revType)).tableName(auditTable.replace("_AUD", ""))
					.entityName(entityName).editRowNo(accountId).columnName(column).oldColumnValue(toText(oldValue))
					.newColumnValue(toText(newValue)).createdAt(reportCreatedAt).dmlTstamp(dmlTstamp).build());
		}

		return reports;
	}

	private boolean isModified(Object value) {

		if (value == null) {
			return false;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}

		return Boolean.parseBoolean(value.toString());
	}

	private String getAction(Integer revType) {

		return switch (revType) {
		case 0 -> "INSERT";
		case 1 -> "UPDATE";
		case 2 -> "DELETE";
		default -> "UNKNOWN";
		};
	}

	private LocalDateTime getRevisionTimestamp(Long rev) {

		Object value = entityManager.createNativeQuery("""
				SELECT REVTSTMP
				FROM REVINFO
				WHERE REV = :rev
				""").setParameter("rev", rev).getSingleResult();

		long timestamp = ((Number) value).longValue();

		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	private String toText(Object value) {
		return value == null ? null : value.toString();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DeltaReport> getAllDeltaReports() {
		return deltaReportRepository.findAll();
	}
}
