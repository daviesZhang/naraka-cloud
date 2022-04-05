package com.davies.naraka.puppeteer.controller;

import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.JpaSpecificationUtils;
import com.davies.naraka.puppeteer.domain.dto.QueryPageDTO;
import com.davies.naraka.puppeteer.domain.dto.ScriptCaseQueryDTO;
import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import com.davies.naraka.puppeteer.repository.ScriptCaseRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@Validated
public class TestController {

    private final ScriptCaseRepository scriptCaseRepository;

    private final JpaSpecificationUtils specificationUtils;

    public TestController(ScriptCaseRepository scriptCaseRepository, JpaSpecificationUtils specificationUtils) {
        this.scriptCaseRepository = scriptCaseRepository;
        this.specificationUtils = specificationUtils;
    }

    @GetMapping("/test")
    @ResponseBody
    public void fileSystemResource(HttpServletResponse response) throws IOException {
        ScriptCase scriptCase = new ScriptCase();
        scriptCase.setProject("test_project");
        scriptCase.setEnvironment("test");
        scriptCase.setName("login");
        scriptCase.setScriptStatus(ScriptStatus.DISABLE);
//        this.scriptCaseRepository.save(scriptCase);


        //return ResponseEntity.ok().body()
    }

    @PostMapping("/list")
    @ResponseBody
    public PageDTO<ScriptCase> fileSystemResource(
            @RequestBody @Valid QueryPageDTO<@Valid ScriptCaseQueryDTO> queryPage
    ) {

        return this.specificationUtils.pageQuery(queryPage, scriptCaseRepository);
    }
}
