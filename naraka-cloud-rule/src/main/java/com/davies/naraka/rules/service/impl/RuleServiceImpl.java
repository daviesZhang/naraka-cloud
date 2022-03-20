package com.davies.naraka.rules.service.impl;

import com.davies.naraka.cloud.common.exception.ObjectAlreadyExistsException;
import com.davies.naraka.rules.domain.entity.Rule;
import com.davies.naraka.rules.repository.RuleRepository;
import com.davies.naraka.rules.service.RuleService;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author davies
 * @date 2022/3/19 19:27
 */
@Service
public class RuleServiceImpl implements RuleService {

    private final RuleRepository repository;

    public RuleServiceImpl(RuleRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    @Modifying
    public void save(Rule rule) {
        boolean exists = repository.exists(Example.of(new Rule().setProject(rule.getProject()).setName(rule.getName())));
        if (exists) {
            throw new ObjectAlreadyExistsException("对象已经存在~");
        }
        repository.save(rule);

    }


}
