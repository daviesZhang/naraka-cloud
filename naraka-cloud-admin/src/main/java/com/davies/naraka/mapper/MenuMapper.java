package com.davies.naraka.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.davies.naraka.domain.bo.MenuBO;
import com.davies.naraka.domain.entity.Menu;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
public interface MenuMapper extends BaseMapper<Menu> {


    List<MenuBO> selectMenuTree();
}
