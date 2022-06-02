package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.jpa.JoinQuery;
import com.davies.naraka.autoconfigure.jpa.JpaSpecificationUtils;
import com.davies.naraka.autoconfigure.jpa.SQLExecuteHelper;
import com.davies.naraka.autoconfigure.jpa.SQLParamsProvider;
import com.davies.naraka.puppeteer.domain.dto.CaseReportQueryDTO;
import com.davies.naraka.puppeteer.domain.entity.CaseReport;
import com.davies.naraka.puppeteer.domain.entity.CaseStep;
import com.davies.naraka.puppeteer.domain.entity.CaseStepHistory;
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

    @Autowired
    private CaseStepHistoryRepository caseStepHistoryRepository;


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

        CaseReport report = new CaseReport();
        report.setScriptCase(scriptCase);
        ClassUtils.copyObject(scriptCase, report);
        CaseStepHistory history = new CaseStepHistory();
        ClassUtils.copyObject(caseStep, history);
        this.scriptCaseRepository.save(scriptCase);
        this.caseStepRepository.save(caseStep);
        this.caseReportRepository.save(report);
        history.setReportId(report.getId());
        this.caseStepHistoryRepository.save(history);
    }


    @Test
    public void findStep() {
        // List<CaseStep> caseSteps = this.caseStepRepository.findAll();
        // caseSteps.forEach(System.out::println);
//        List<TestBO> scriptCases = this.scriptCaseRepository.testBoo();
        // Transformers.aliasToBean()
//        scriptCases.forEach(System.out::println);
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
        queryCase.setProject(new QueryField<>(QueryFilterType.LIKE, "te2st"));
        queryCase.setEnvironment("test");
        queryCase.setName(new QueryField<>(QueryFilterType.CONTAINS, Lists.newArrayList("12345", "login")));
//        queryCase.setName(new QueryField<>(QueryFilterType.CONTAINS, Lists.newArrayList("xcx","login")));
        queryCase.setStepName("First");
        QueryField<LocalDateTime> asc = new QueryField<>(QueryFilterType.ASC, null);
        QueryField<LocalDateTime> now = new QueryField<>(QueryFilterType.LE, LocalDateTime.now());
        queryCase.setCreatedTime(Lists.newArrayList(asc, now));
        queryCase.setUpdatedTime(new QueryField<>(QueryFilterType.DESC, null));

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
        @ColumnName("name")
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

    @Autowired
    private SQLExecuteHelper SQLExecuteHelper;

    @Test
    public void sqlTest() throws Throwable {

        CaseReportQueryDTO queryDTO = new CaseReportQueryDTO();
        queryDTO.setName("test");
        queryDTO.setCreatedBy("xxx");
        //QueryHelper queryHelper = query(queryDTO);
        //Query reportQuery = entityManager.createQuery("select c,h from CaseReport c left join CaseStepHistory h  on c.id = h.reportId" + queryHelper.getQuerySql());
        // Function<Query, Query> function = queryHelper.getParamsFunction();
        //if (function != null) {
        //    function.apply(reportQuery);
        //}
        //List list = reportQuery.getResultList();
        //SimpleQuery query = new SimpleQuery();
        //Page<Object> objectPage = query.getPage(entityManager, queryDTO, "select c,h from CaseReport c left join CaseStepHistory h  on c.id = h.reportId", null);
//        List list = caseReportRepository.queryList(queryDTO, "from CaseReport");

        List<CaseReport> list = SQLExecuteHelper.getList(SQLParamsProvider.query(queryDTO), CaseReport.class);
//
        for (Object o : list) {
            System.err.println(o);
        }
        //this.scriptCaseRepository.fi
    }


}
