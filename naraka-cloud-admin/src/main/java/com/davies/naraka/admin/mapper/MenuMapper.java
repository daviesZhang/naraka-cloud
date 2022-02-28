package com.davies.naraka.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.davies.naraka.admin.domain.bo.MenuBO;
import com.davies.naraka.admin.domain.entity.Menu;

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
