package com.ram.service;

import java.util.List;
import com.ram.model.DeltaReport;

public interface DeltaReportService {

	void generateAndSaveDeltaReport();

	List<DeltaReport> getAllDeltaReports();
}