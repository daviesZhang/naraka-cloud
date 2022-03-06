package com.davies.naraka.admin.controller.system;


import com.davies.naraka.admin.domain.dto.system.*;
import com.davies.naraka.admin.domain.entity.Authority;
import com.davies.naraka.admin.domain.entity.Menu;
import com.davies.naraka.admin.domain.enums.CategoryType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import com.davies.naraka.admin.service.ICategoryTreeService;
import com.davies.naraka.admin.service.IMenuService;
import com.davies.naraka.admin.service.IUserService;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.security.HasUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/2/10 2:39 PM
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    private final IMenuService menuService;


    private final ICategoryTreeService categoryTreeService;

    @Autowired
    private CurrentUserNameSupplier currentUserNameSupplier;
    @Autowired
    private IUserService userService;

    public MenuController(IMenuService menuService, ICategoryTreeService categoryTreeService) {
        this.menuService = menuService;
        this.categoryTreeService = categoryTreeService;
    }

    @PostMapping("/list")
    public List<MenuDTO> menus() {

        return this.menuService.menus().stream().map(menu -> ClassUtils.copyObject(menu, new MenuDTO()))
                .collect(Collectors.toList());
    }

    /**
     * 当前用户拥有的菜单列表
     *
     * @return
     */
    @GetMapping("/own")
    @HasUser
    public List<UserMenuDTO> ownMenus() {
        List<String> menus = this.userService
                .getUserAuthorityList(currentUserNameSupplier.get(), ResourceType.MENU)
                .stream()
                .map(Authority::getResource)
                .collect(Collectors.toList());
        return this.menuService.menus()
                .stream().filter(menu -> menus.contains(menu.getUrl()))
                .map(menu -> ClassUtils.copyObject(menu, new UserMenuDTO()))
                .collect(Collectors.toList());
    }


    @PutMapping("/move")
    public ResponseEntity<Void> move(@RequestBody @Validated MoveDTO move) {
        this.categoryTreeService.moveTree(move.getFrom(), move.getTo(), CategoryType.MENU);
        return ResponseEntity.ok().build();
    }


    @PutMapping()
    public ResponseEntity<Void> updateMenu(@RequestBody @Validated MenuUpdateDTO menu) {

        this.menuService.updateById(ClassUtils.copyObject(menu, new Menu()));
        return ResponseEntity.ok().build();
    }


    @PostMapping()
    public Integer createMenu(@RequestBody @Validated MenuCreateDTO menuCreateDTO) {
        Menu menu = ClassUtils.copyObject(menuCreateDTO, new Menu());
        return this.menuService.createMenu(menu, menuCreateDTO.getParent());
    }


    @DeleteMapping()
    public ResponseEntity<Void> delete(@RequestParam Integer id) {
        this.menuService.delete(id);
        return ResponseEntity.ok().build();
    }


}
