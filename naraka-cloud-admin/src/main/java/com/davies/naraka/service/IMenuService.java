package com.davies.naraka.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.domain.bo.MenuBO;
import com.davies.naraka.domain.entity.Menu;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 添加一个菜单
     * @param menu
     * @param parent
     */
    Integer createMenu(Menu menu,Integer parent);


    /**
     * 获取所有菜单列表,并包含他的上级关系
     * @return
     */
    List<MenuBO> menus();


    void delete(Integer id);

}
