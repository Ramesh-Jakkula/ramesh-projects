package com.ram.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.model.DeltaReport;
import com.ram.service.DeltaReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/delta-reports")
@RequiredArgsConstructor
public class DeltaReportController {

	private final DeltaReportService deltaReportService;

	@PostMapping("/generate")
	public ResponseEntity<String> generateAndSaveDeltaReport() {

		deltaReportService.generateAndSaveDeltaReport();

		return ResponseEntity.ok("Delta reports generated successfully");
	}

	@GetMapping
	public ResponseEntity<List<DeltaReport>> getAllDeltaReports() {

		return ResponseEntity.ok(deltaReportService.getAllDeltaReports());
	}
}