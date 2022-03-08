package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.domain.entity.CaseStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author sycho
 */
public interface CaseStepRepository extends JpaRepository<CaseStep, Long>, JpaSpecificationExecutor<CaseStep> {
}