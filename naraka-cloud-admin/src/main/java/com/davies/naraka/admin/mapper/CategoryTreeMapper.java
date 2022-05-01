package com.davies.naraka.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.davies.naraka.admin.domain.entity.CategoryTree;
import com.davies.naraka.admin.domain.enums.CategoryType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
public interface CategoryTreeMapper extends BaseMapper<CategoryTree> {


    /**
     * 将ancestor为ancestors集合且 descendant为 descendants 集合的记录距离减1
     * @param ancestors 祖节点
     * @param descendants 子节点
     * @param type  关系类型
     */

    void subDistance(@Param("ancestors") List<Integer> ancestors,
                     @Param("descendants") List<Integer> descendants,
                     @Param("type") CategoryType type,
                     @Param("distance") Integer distance);
}
