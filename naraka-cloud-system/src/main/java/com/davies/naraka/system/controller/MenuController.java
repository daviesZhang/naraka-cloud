package com.davies.naraka.system.controller;

import com.google.common.collect.Lists;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author davies
 * @date 2022/6/5 21:41
 */
@RestController
@Validated
@RequestMapping("/menu")
public class MenuController {


    @GetMapping("/own")
    public List<String> menus() {
        return Lists.newArrayList();
    }
}
