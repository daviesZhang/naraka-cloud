package com.davies.naraka.rules.service;

import com.davies.naraka.rules.domain.entity.Rule;

/**
 * @author davies
 * @date 2022/3/19 19:27
 */
public interface RuleService {


    /**
     * 保存规则,传入ID视为更新
     *
     * @param rule
     */
    void save(Rule rule);


}
