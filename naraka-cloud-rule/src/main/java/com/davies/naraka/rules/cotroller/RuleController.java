package com.davies.naraka.rules.cotroller;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.jpa.JpaSpecificationUtils;
import com.davies.naraka.rules.domain.dto.RuleCreateDTO;
import com.davies.naraka.rules.domain.dto.RuleDTO;
import com.davies.naraka.rules.domain.dto.RuleQueryDTO;
import com.davies.naraka.rules.domain.dto.RuleUpdateDTO;
import com.davies.naraka.rules.domain.entity.Rule;
import com.davies.naraka.rules.repository.RuleRepository;
import com.davies.naraka.rules.service.RuleService;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.kie.api.builder.KieFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/3/19 19:02
 */
@RestController
@Validated
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleRepository repository;

    @Autowired
    private JpaSpecificationUtils specificationUtils;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Validated RuleCreateDTO ruleCreateDTO) {
        Rule rule = ClassUtils.copyObject(ruleCreateDTO, new Rule());
        this.checkRule(rule.getContent());
        ruleService.save(rule);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/list")
    public List<RuleDTO> list(@RequestBody RuleQueryDTO queryDTO) {

        return repository.findAll(specificationUtils.specification(queryDTO)).stream()
                .map(rule -> ClassUtils.copyObject(rule, new RuleDTO()))
                .collect(Collectors.toList());

    }


    @PutMapping
    public ResponseEntity<Void> delete(@RequestBody @Validated RuleUpdateDTO ruleUpdateDTO) {
        Rule rule = ClassUtils.copyObject(ruleUpdateDTO, new Rule());
        this.checkRule(rule.getContent());
        ruleService.save(rule);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public void delete(@RequestParam() long id) {
        repository.deleteById(id);
    }

    private void checkRule(String text) {
        KieFileSystem kieFileSystem = new KieFileSystemImpl();
        kieFileSystem.writePomXML(text.getBytes(StandardCharsets.UTF_8));


    }
}
