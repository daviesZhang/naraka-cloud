package com.davies.naraka.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.davies.naraka.domain.entity.Authority;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
public interface AuthorityMapper extends BaseMapper<Authority> {

    /**
     * 连表查询用户信息和角色信息的分页数据
     * @param page
     * @param ew
     * @return
     */
    Page<Authority> selectAuthorityPage(Page<Authority> page, @Param(Constants.WRAPPER) Wrapper<Authority> ew);

}
