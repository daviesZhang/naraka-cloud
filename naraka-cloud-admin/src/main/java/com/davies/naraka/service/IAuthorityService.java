package com.davies.naraka.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.domain.entity.Authority;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
public interface IAuthorityService extends IService<Authority> {


    void createAuthority(Authority authority);


    /**
     * 删除权限,以及和角色的关联数据
     * @param ids
     */
    void delete(List<Integer> ids);


    /**
     * 用户表 left join 角色表 后的结果
     * @param page
     * @param wrapper
     * @return
     */
    Page<Authority> authorityList(Page<Authority> page, Wrapper<Authority> wrapper);
}
