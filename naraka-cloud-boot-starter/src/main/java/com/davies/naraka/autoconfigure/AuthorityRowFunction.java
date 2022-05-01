package com.davies.naraka.autoconfigure;

import com.davies.naraka.cloud.common.domain.AuthorityRow;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author davies
 * @date 2022/4/8 22:03
 */
public interface AuthorityRowFunction extends BiFunction<String, String, List<AuthorityRow>> {


    /**
     * 根据 resource和principal 获取他的row数据权限过滤配置
     *
     * @param resource  资源
     * @param principal 用户
     * @return 行权限配置
     */
    @Override
    List<AuthorityRow> apply(String resource, String principal);

}
