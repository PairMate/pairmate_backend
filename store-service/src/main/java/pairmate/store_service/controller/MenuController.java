package pairmate.store_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.store_service.service.MenuService;
import pairmate.store_service.dto.MenuResponse;

import java.util.List;

@Controller
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "메뉴 관련 API")
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/recommend/random")
    public List<MenuResponse> getRandomMenus(@RequestParam(defaultValue = "1") int count) {
        return menuService.getRandomMenus(count);
    }

    @GetMapping("/{menuId}")
    public MenuResponse getMenu(@PathVariable("storeId") Long storeId, @PathVariable("menuId") Long menuId) {
        return menuService.getMenuById(menuId);
    }


}
