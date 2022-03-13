package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.jpa.JoinQuery;
import com.davies.naraka.autoconfigure.jpa.JpaSpecificationUtils;
import com.davies.naraka.puppeteer.domain.bo.TestBO;
import com.davies.naraka.puppeteer.domain.entity.CaseReport;
import com.davies.naraka.puppeteer.domain.entity.CaseStep;
import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import com.davies.naraka.puppeteer.domain.enums.StepAction;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
class ScriptCaseRepositoryTest {

    @Autowired
    private ScriptCaseRepository scriptCaseRepository;

    @Autowired
    private CaseStepRepository caseStepRepository;

    @Autowired
    private CaseReportRepository caseReportRepository;

    @Autowired
    private JpaSpecificationUtils specificationUtils;

    @Test
    @Transactional
    @Rollback(value = false)
    public void saveTest() {
        ScriptCase scriptCase = new ScriptCase();
        scriptCase.setProject("test_project");
        scriptCase.setEnvironment("test");
        scriptCase.setName("login");
        scriptCase.setScriptStatus(ScriptStatus.DISABLE);

        scriptCase.setCreatedTime(LocalDateTime.now());
        CaseStep caseStep = new CaseStep();
        caseStep.setScriptCase(scriptCase);
        caseStep.setName("002");
        caseStep.setAction(StepAction.CLOSE);
        caseStep.setCreatedTime(LocalDateTime.now().minusDays(2L));
        scriptCase.setSteps(Collections.singletonList(caseStep));
        this.scriptCaseRepository.save(scriptCase);
        this.caseStepRepository.save(caseStep);
    }


    @Test
    public void findStep() {
        // List<CaseStep> caseSteps = this.caseStepRepository.findAll();
        // caseSteps.forEach(System.out::println);
        List<TestBO> scriptCases = this.scriptCaseRepository.testBo();
        // Transformers.aliasToBean()
        scriptCases.forEach(System.out::println);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void saveReportTest() {

        CaseReport report = new CaseReport();
        ScriptCase scriptCase = new ScriptCase();
//        scriptCase.setName("login");
        scriptCase.setId(10L);
        /*Long scriptId = this.scriptCaseRepository.findOne(Example.of(scriptCase))
                .map(ScriptCase::getId).orElse(null);
        report.setScriptCase(scriptId);*/
        this.caseReportRepository.save(report);
    }

    @Test
    @Rollback(value = false)
    public void specificationUtilsTest() {

        QueryCase queryCase = new QueryCase();
        queryCase.setProject(new QueryField<>(QueryFilterType.LIKE,"te2st"));
        queryCase.setEnvironment("test");
        queryCase.setName(new QueryField<>(QueryFilterType.CONTAINS, Lists.newArrayList("12345","login")));
//        queryCase.setName(new QueryField<>(QueryFilterType.CONTAINS, Lists.newArrayList("xcx","login")));
        queryCase.setStepName("First");
        QueryField<LocalDateTime> asc = new QueryField<>(QueryFilterType.ORDER_ASC, null);
        QueryField<LocalDateTime> now = new QueryField<>(QueryFilterType.LESSTHANEQUAL, LocalDateTime.now());
        queryCase.setCreatedTime(Lists.newArrayList(asc, now));
        queryCase.setUpdatedTime(new QueryField<>(QueryFilterType.ORDER_DESC, null));

        Specification<ScriptCase> specification = specificationUtils.specification(queryCase);
        List<ScriptCase> cases = this.scriptCaseRepository.findAll(specification);
        Assertions.assertFalse(cases.isEmpty());

    }

    @Data
    @NoArgsConstructor
    public static class QueryCase {

        private QueryField<String> project;


        private String environment;

        @JoinQuery(attributeName = "steps")
        @ColumnName(name = "name")
        private String stepName;

        private QueryField<List<String>> name;

        private List<QueryField<LocalDateTime>> createdTime;

        private QueryField<String> updatedTime;

    }

    @Test
    public void selectJoinTest() {

        Specification<CaseReport> specification = new Specification<CaseReport>() {
            @Override
            public Predicate toPredicate(Root<CaseReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<CaseReport, ScriptCase> join = root.join("scriptCase", JoinType.LEFT);
                Predicate login = cb.equal(join.get("name"), "login");
                Predicate project = query.where(cb.equal(join.get("project"), "te2st")).getRestriction();
                return cb.and(project, login);
            }
        };
        List<CaseReport> caseReports = this.caseReportRepository.findAll(specification);
        Assertions.assertTrue(caseReports.isEmpty());

    }

    @Test
    public void selectTest() {
        Specification<ScriptCase> specification = new Specification<ScriptCase>() {
            @Override
            public Predicate toPredicate(Root<ScriptCase> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate test = criteriaBuilder.equal(root.get("name"), "login");
                Join<ScriptCase, CaseStep> join = root.join("steps", JoinType.LEFT);
//                join.on(criteriaBuilder.equal(root.get("id"), join.get("script_case_id")));
                Predicate first = criteriaBuilder.equal(join.get("name"), "First");
                return criteriaBuilder.and(test, first);
            }
        };

        List<ScriptCase> scriptCaseList = this.scriptCaseRepository.findAll(specification);
        Assertions.assertTrue(scriptCaseList.size() > 0);
        scriptCaseList.forEach(scriptCase -> System.out.println("scriptCase = " + scriptCase));
    }
}