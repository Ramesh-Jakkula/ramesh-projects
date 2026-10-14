
package com.ram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ram.model.DeltaReport;
@Repository
public interface DeltaReportRepository extends JpaRepository<DeltaReport, Long> {

}
