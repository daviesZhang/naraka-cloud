package com.davies.naraka.puppeteer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class TestController {


    @GetMapping("/test")
    @ResponseBody
    public void fileSystemResource(HttpServletResponse response) throws IOException {


        //return ResponseEntity.ok().body()
    }
}
