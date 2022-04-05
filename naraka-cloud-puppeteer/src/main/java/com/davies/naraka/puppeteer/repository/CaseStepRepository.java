package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.domain.entity.CaseStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author sycho
 */
@Repository
public interface CaseStepRepository extends JpaRepository<CaseStep, Long>, JpaSpecificationExecutor<CaseStep> {
}