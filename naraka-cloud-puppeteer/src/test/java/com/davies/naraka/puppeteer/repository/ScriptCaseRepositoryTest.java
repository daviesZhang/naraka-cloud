package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.annotation.ColumnName;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import com.davies.naraka.autoconfigure.jpa.JoinQuery;
import com.davies.naraka.autoconfigure.jpa.JpaSpecificationUtils;
import com.davies.naraka.cloud.common.StringConstants;
import com.davies.naraka.puppeteer.annotation.QueryParams;
import com.davies.naraka.puppeteer.domain.bo.TestBO;
import com.davies.naraka.puppeteer.domain.dto.CaseReportQueryDTO;
import com.davies.naraka.puppeteer.domain.entity.CaseReport;
import com.davies.naraka.puppeteer.domain.entity.CaseStep;
import com.davies.naraka.puppeteer.domain.entity.CaseStepHistory;
import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import com.davies.naraka.puppeteer.domain.enums.StepAction;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

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


    @Autowired
    private EntityManager entityManager;

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
        List<TestBO> scriptCases = this.scriptCaseRepository.testBoo();
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
        queryCase.setProject(new QueryField<>(QueryFilterType.LIKE, "te2st"));
        queryCase.setEnvironment("test");
        queryCase.setName(new QueryField<>(QueryFilterType.CONTAINS, Lists.newArrayList("12345", "login")));
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


    @Test
    public void sqlTest() throws Throwable {
//        Specification<CaseReport> specification = new Specification<CaseReport>() {
//            @Override
//            public Predicate toPredicate(Root<CaseReport> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//
//                Root<CaseStepHistory > join = query.from(CaseStepHistory.class);
//                return query.select(join.join("reportId",JoinType.LEFT)).where(criteriaBuilder.equal(join.get("action"),"xxx"),
//                                criteriaBuilder.equal(root.get("id"),join.get("reportId")))
//                        .getRestriction();
//
//            }
//        };
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        Query reportQuery = entityManager.createQuery("SELECT sc.name,sch.updatedTime " +
//                "from ScriptCase sc left join " +
//                "CaseStepHistory sch on " +
//                "sc.name=sch.name");
//        reportQuery.set
//        Root<CaseReport> root = reportQuery.from(CaseReport.class);
//        //Root<CaseStepHistory> historyRoot = reportQuery.from(CaseStepHistory.class);
//        Subquery<CaseStepHistory> subquery=reportQuery.subquery(CaseStepHistory.class);
//        Root<CaseStepHistory> historyRoot=subquery.from(CaseStepHistory.class);
//        Join<CaseStepHistory, CaseReport> join = historyRoot.join("reportId");

        // reportQuery.multiselect(root, historyRoot);
        CaseReportQueryDTO queryDTO = new CaseReportQueryDTO();
        queryDTO.setName("test");
        queryDTO.setCreatedBy("xxx");
        QueryHelper queryHelper = query(queryDTO);
        Query reportQuery = entityManager.createQuery("select c,h from CaseReport c left join CaseStepHistory h  on c.id = h.reportId" + queryHelper.getQuerySql());
        Function<Query, Query> function = queryHelper.getParamsFunction();
        if (function != null) {
            function.apply(reportQuery);
        }
        List list = reportQuery.getResultList();
        SimpleQuery query = new SimpleQuery();
        Page<Object> objectPage = query.getPage(entityManager, queryDTO, "select c,h from CaseReport c left join CaseStepHistory h  on c.id = h.reportId", null);


        for (Object o : list) {
            System.err.println(o);
        }
        //this.scriptCaseRepository.fi
    }

    public class SimpleQuery {
        private final Pattern FROM_PATTERN = Pattern.compile("select.*?from", Pattern.CASE_INSENSITIVE);
        private final String COUNT_FROM = "SELECT COUNT(*) FROM";

        public <T> Page<T> getPage(EntityManager entityManager, Object queryParams, String from, Class<T> tClass) {
            QueryHelper queryHelper = query(queryParams);
            String countFrom = FROM_PATTERN
                    .matcher(from)
                    .replaceAll(COUNT_FROM);
            TypedQuery<Long> countQuery = entityManager.createQuery(countFrom + queryHelper.getQuerySql(), Long.class);
            Function<Query, Query> function = queryHelper.getParamsFunction();
            if (function != null) {
                function.apply(countQuery);
            }
            Long count = countQuery.getSingleResult();
            Pageable pageable = queryHelper.getPageable();
            if (count <= 0) {
                return Page.empty(pageable);
            }
            if (tClass != null) {
                TypedQuery<T> query = entityManager.createQuery(from + queryHelper.getQuerySql(), tClass);
                List<T> list = query.getResultList();
                return new PageImpl<>(list, pageable, count);
            }
            Query query = entityManager.createQuery(from + queryHelper.getQuerySql());
            List list = query.getResultList();
            return new PageImpl<>(list, pageable, count);
        }
    }

    public QueryHelper query(Object o) {
        String PAGE = "page";
        String SIZE = "size";
        String SORT = "sort";
        String DESC = "desc";
        Class<?> oClass = o.getClass();
        Field[] fields = oClass.getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        String AND = " AND ";
        String FIELD = "%s ";
        boolean isNotBlank = false;
        ParameterLink queryFunction = null;
        for (Field field : fields) {
            QueryParams queryParams = field.getDeclaredAnnotation(QueryParams.class);
            if (null != queryParams && queryParams.skip()) {
                continue;
            }
            Object params;
            try {
                MethodHandle handle = MethodHandles.lookup().findVirtual(oClass, "get" + capitalize(field.getName()), MethodType.methodType(String.class));
                params = handle.invoke(o);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            if (params == null) {
                continue;
            }
            if (isNotBlank) {
                builder.append(AND);
            }
            if (!isNotBlank) {
                isNotBlank = true;
            }
            String alias = field.getName();

            if (queryParams != null && !Strings.isNullOrEmpty(queryParams.alias())) {
                alias = queryParams.alias();
            }
            String paramsTemplate = filterType(queryParams == null ? QueryFilterType.EQUALS : queryParams.filterType());
            builder.append(Strings.lenientFormat(FIELD, alias))
                    .append(Strings.lenientFormat(paramsTemplate, field.getName()));
            queryFunction = new ParameterLink(field.getName(), params, queryFunction);
        }
        String querySql;
        if (isNotBlank) {
            querySql = builder.insert(0, " where ").toString();
        } else {
            querySql = StringConstants.EMPTY;
        }
        return new QueryHelper(querySql, queryFunction);
    }


    private String filterType(QueryFilterType filterType) {
        String PARAMS = " :%s";
        switch (filterType) {
            case CONTAINS:
                return " in ( " + PARAMS + " )";
            case NOT_CONTAINS:
                return " not in ( " + PARAMS + " )";
            case GREATERTHANEQUAL:
                return " >= " + PARAMS;
            case EQUALS:
            default:
                return " = " + PARAMS;
        }

    }

    private String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    public class ParameterLink implements Function<Query, Query> {
        protected final ParameterLink previous;
        private final String name;
        private final Object params;

        protected ParameterLink(String name, Object params, ParameterLink previous) {
            this.previous = previous;
            this.name = name;
            this.params = params;
        }

        @Override
        public Query apply(Query query) {
            return (previous != null ? previous.apply(query) : query).setParameter(name, params);
        }
    }


    public class QueryHelper {

        private String querySql;

        private Pageable pageable;

        private Function<Query, Query> paramsFunction;

        public Pageable getPageable() {
            return pageable;
        }

        public void setPageable(Pageable pageable) {
            this.pageable = pageable;
        }

        public QueryHelper(String querySql, Function<Query, Query> paramsFunction) {
            this.querySql = querySql;
            this.paramsFunction = paramsFunction;
        }

        public String getQuerySql() {
            return querySql;
        }

        public void setQuerySql(String querySql) {
            this.querySql = querySql;
        }

        public Function<Query, Query> getParamsFunction() {
            return paramsFunction;
        }

        public void setParamsFunction(Function<Query, Query> paramsFunction) {
            this.paramsFunction = paramsFunction;
        }
    }
}