package com.davies.naraka.puppeteer.controller;

import com.davies.naraka.puppeteer.domain.entity.ScriptCase;
import com.davies.naraka.puppeteer.domain.enums.ScriptStatus;
import com.davies.naraka.puppeteer.repository.ScriptCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class TestController {

    @Autowired
    private ScriptCaseRepository scriptCaseRepository;

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
    @GetMapping("/list")
    @ResponseBody
    public List<ScriptCase> fileSystemResource() {
        return this.scriptCaseRepository.findAll();
    }
}
