
package com.ram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMER_ACCOUNT_DELTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeltaReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DELTA_REPORT_ID")
	private Long deltaReportId;

	@Column(name = "ACCOUNT_ID", nullable = false)
	private Long accountId;

	@Column(name = "ACCOUNT_NUMBER", length = 30)
	private String accountNumber;

	@Column(name = "REV", nullable = false)
	private Long rev;

	@Column(name = "REVTYPE", nullable = false)
	private Integer revType;

	@Column(name = "MODE_OF_OPERATION", nullable = false, length = 15)
	private String modeOfOperation;

	@Column(name = "TABLE_NAME", nullable = false, length = 128)
	private String tableName;

	@Column(name = "ENTITY_NAME", nullable = false, length = 128)
	private String entityName;

	@Column(name = "EDIT_ROW_NO", nullable = false)
	private Long editRowNo;

	@Column(name = "COLUMN_NAME", nullable = false, length = 128)
	private String columnName;

	@Column(name = "OLD_COLUMN_VALUE", length = 4000)
	private String oldColumnValue;

	@Column(name = "NEW_COLUMN_VALUE", length = 4000)
	private String newColumnValue;

	@Column(name = "CREATED_AT", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "DML_TSTAMP", nullable = false)
	private LocalDateTime dmlTstamp;
}
