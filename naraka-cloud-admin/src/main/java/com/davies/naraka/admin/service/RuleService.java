package com.davies.naraka.admin.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * @author davies
 * @date 2022/3/20 10:48
 */
@FeignClient(value = "naraka-cloud-gateway", path = "/rule")
public interface RuleService {


    @RequestMapping(method = RequestMethod.POST, value = "/rules/list")
    public List<Object> getList(@RequestBody Map<String, String> object);
}
