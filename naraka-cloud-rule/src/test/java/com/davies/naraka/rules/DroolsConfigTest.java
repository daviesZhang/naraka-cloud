package com.davies.naraka.rules;

import com.davies.naraka.rules.domain.entity.Rule;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author davies
 * @date 2022/3/17 19:53
 */
@SpringBootTest
class DroolsConfigTest {


    @Autowired
    private KieContainer kieContainer;


    @Test
    public void fire() {
        // KieServices.Factory.get().getKieClasspathContainer("")
        KieSession session = kieContainer.newKieSession();
        // kieContainer.getKieBase().getKiePackage().getRules()
        Rule rule = new Rule();

        session.insert(rule);
        // session.()
        assertEquals("success", rule.getName());

    }

}