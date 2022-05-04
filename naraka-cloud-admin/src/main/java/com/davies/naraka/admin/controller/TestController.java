package com.davies.naraka.admin.controller;

import com.davies.naraka.admin.config.RedisIdentifierGenerator;
import com.davies.naraka.admin.service.RuleService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author davies
 * @date 2022/2/26 5:14 PM
 */
@RestController
@Slf4j
public class TestController {


    @Autowired
    private RIdGenerator rIdGenerator;

    @Value("${user.age}")
    private Integer age;

    @Value("${server.port:8080}")
    private Integer port;
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RedissonClient redissonClient;


    @Autowired
    private RedisIdentifierGenerator redisIdentifierGenerator;

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


    @GetMapping("/test/id")
    public Object getId(@RequestParam(required = false, value = "count", defaultValue = "1") Integer count) {

//        for (int i = 0; i < 1000; i++) {
//            log.info(String.valueOf(redisIdentifierGenerator.nextUUID(this)));
//        }
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                redisIdentifierGenerator.nextUUID(this);
            }
        }
        return String.valueOf(redisIdentifierGenerator.nextUUID(this));
    }
}
