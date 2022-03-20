package com.davies.naraka.admin.controller;

import com.davies.naraka.admin.service.RuleService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
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

    @Autowired
    private RuleService ruleService;

    @GetMapping(value = {"/test", "/bing/test"})
    public String test() {
        log.info("port:{}", port);
        return Strings.lenientFormat("age:%s,port:%s,username:%s,usernameHeader:%s"
                , age, port, request.getRemoteUser(), request.getHeader("username"));
        //return "age:" + age + "port:"+port+"username:"+request.getRemoteUser();
    }

    @GetMapping("/test/list")
    public Object getList() {

        return this.ruleService.getList(Maps.newHashMap());
    }

}
