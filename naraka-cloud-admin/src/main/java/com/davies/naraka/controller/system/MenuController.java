package com.davies.naraka.controller.system;


import com.davies.naraka.common.ClassUtils;
import com.davies.naraka.domain.enums.CategoryType;
import com.davies.naraka.domain.enums.CrudType;
import com.davies.naraka.domain.dto.system.MenuCreateDTO;
import com.davies.naraka.domain.dto.system.MenuDTO;
import com.davies.naraka.domain.dto.system.MenuUpdateDTO;
import com.davies.naraka.domain.dto.system.MoveDTO;
import com.davies.naraka.domain.entity.Menu;
import com.davies.naraka.service.ICategoryTreeService;
import com.davies.naraka.service.IMenuService;
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

    public MenuController(IMenuService menuService, ICategoryTreeService categoryTreeService) {
        this.menuService = menuService;
        this.categoryTreeService = categoryTreeService;
    }

    @PostMapping("/list")
    
    public List<MenuDTO> menus() {

        return this.menuService.menus().stream().map(menu -> ClassUtils.copyObject(menu, new MenuDTO()))
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

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        this.menuService.delete(id);
        return ResponseEntity.ok().build();
    }

}
