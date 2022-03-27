package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.domain.bo.TestBO;
import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author sycho
 */
public interface ScriptCaseRepository extends JpaRepository<ScriptCase, Long>, JpaSpecificationExecutor<ScriptCase> {


    @Query("SELECT new com.davies.naraka.puppeteer.domain.bo.TestBO(name, updatedTime) from ScriptCase")
    List<TestBO> testBo();

    @Query("SELECT new com.davies.naraka.puppeteer.domain.bo.TestBO(sc.name, sch.updatedTime) from " +
            "ScriptCase sc left join CaseStepHistory sch on sc.name=sch.name")
    List<TestBO> testBoo();
}