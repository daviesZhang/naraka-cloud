package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import com.davies.naraka.admin.domain.enums.CategoryType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import com.davies.naraka.admin.mapper.MenuMapper;
import com.davies.naraka.admin.domain.bo.MenuBO;
import com.davies.naraka.admin.domain.entity.Authority;
import com.davies.naraka.admin.domain.entity.Menu;
import com.davies.naraka.admin.service.IAuthorityService;
import com.davies.naraka.admin.service.ICategoryTreeService;
import com.davies.naraka.admin.service.IMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {


    private final ICategoryTreeService categoryTreeService;

    private final IAuthorityService authorityService;



    public MenuServiceImpl(ICategoryTreeService categoryTreeService, IAuthorityService authorityService) {
        this.categoryTreeService = categoryTreeService;
        this.authorityService = authorityService;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public Integer createMenu(Menu menu, Integer parent) {
        Preconditions.checkArgument(categoryTreeService.isRoot(parent) || null != baseMapper.selectById(parent)
                , "parent:%s not exist", parent);
        save(menu);
        categoryTreeService.insertNode(menu.getId(), parent, CategoryType.MENU);
        return menu.getId();
    }

    @Override
    public List<MenuBO> menus() {
        return baseMapper.selectMenuTree();
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void delete(Integer id) {
        Menu menu = getById(id);
        if (null==menu){
            return;
        }
        List<Integer> authorities = authorityService.list(new QueryWrapper<>(new Authority().setResource(menu.getUrl())
                .setResourceType(ResourceType.MENU)))
                .stream().map(Authority::getId).collect(Collectors.toList());
        if (!authorities.isEmpty()) {
            authorityService.delete(authorities);
        }
        baseMapper.deleteById(id);
        categoryTreeService.delete(id, CategoryType.MENU);

    }
}
