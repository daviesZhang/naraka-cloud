package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author sycho
 */
public interface ScriptCaseRepository extends JpaRepository<ScriptCase, Long>, JpaSpecificationExecutor<ScriptCase> {
}