package com.davies.naraka.rules;

import com.davies.naraka.rules.domain.entity.Rule;
import com.davies.naraka.rules.repository.RuleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author davies
 * @date 2022/3/19 20:14
 */
@SpringBootTest
@ActiveProfiles("dev")
public class DroolsServiceTest {

    @Autowired
    private RuleRepository repository;

//    @Autowired
//    private KieContainer kieContainer;

    @Test
    public void dbTest() {

        String context = "package com.rules\nimport com.davies.naraka.rules.domain.entity.Rule\nrule \"testdb\"\nwhen\nruleName: Rule(name==\"db\");\nthen\nruleName.setName(\"success\");\nend";
        List<Rule> list = repository.findAll();
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        // for (Rule rule : list) {
        //Resource resource = ResourceFactory.newReaderResource(new StringReader(context));
        // kieFileSystem.write("src/main/resources/rules/" + "rule" + ".drl", context.getBytes(StandardCharsets.UTF_8));
        // kieFileSystem.write(resource);
//        kieFileSystem.writePomXML(context);

        //}

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(context, ResourceType.DRL);

        kieServices.newKieBuilder(kieFileSystem).buildAll();
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        // KieSession kieSession = kieContainer.newKieSession();
        KieSession kieSession = kieHelper.build().newKieSession();
        Rule rule = new Rule();
        rule.setName("db");
        kieSession.insert(rule);
        kieSession.fireAllRules();
        Assertions.assertEquals("success", rule.getName());

    }


    @Test
    public void jpaTest() {


    }
}
