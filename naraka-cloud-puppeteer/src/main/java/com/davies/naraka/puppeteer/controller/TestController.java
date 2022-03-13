package com.davies.naraka.puppeteer.controller;

import com.davies.naraka.autoconfigure.domain.PageDTO;
import com.davies.naraka.autoconfigure.jpa.JpaSpecificationUtils;
import com.davies.naraka.puppeteer.domain.dto.QueryPageDTO;
import com.davies.naraka.puppeteer.domain.dto.ScriptCaseQueryDTO;
import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import com.davies.naraka.puppeteer.repository.ScriptCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@Validated
public class TestController {

    @Autowired
    private ScriptCaseRepository scriptCaseRepository;

    @Autowired
    private JpaSpecificationUtils specificationUtils;

    @GetMapping("/test")
    @ResponseBody
    public void fileSystemResource(HttpServletResponse response) throws IOException {
        ScriptCase scriptCase = new ScriptCase();
        scriptCase.setProject("test_project");
        scriptCase.setEnvironment("test");
        scriptCase.setName("login");
        scriptCase.setScriptStatus(ScriptStatus.DISABLE);
        this.scriptCaseRepository.save(scriptCase);


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
