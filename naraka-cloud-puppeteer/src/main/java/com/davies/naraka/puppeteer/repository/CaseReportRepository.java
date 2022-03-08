package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.domain.entity.CaseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CaseReportRepository extends JpaRepository<CaseReport, Long>, JpaSpecificationExecutor<CaseReport> {
}