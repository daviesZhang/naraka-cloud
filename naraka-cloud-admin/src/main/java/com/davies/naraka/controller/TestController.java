package com.davies.naraka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author davies
 * @date 2022/2/26 5:14 PM
 */
@RestController
@Slf4j
public class TestController {


    @Value("${user.age}")
    private Integer age;

    @Value("${server.port:8080}")
    private Integer port;
    @Autowired
    private HttpServletRequest request;

    @GetMapping(value = {"/test", "/bing/test"})
    public String test() {
        log.info("port:{}", port);
        return "age:" + age + "port:"+port+request.getRemoteUser();
    }
}
