package pairmate.store_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pairmate.store_service.service.MenuService;
import pairmate.store_service.web.dto.MenuResponse;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/recommend/random")
    public List<MenuResponse> getRandomMenus(@RequestParam(defaultValue = "1") int count) {
        return menuService.getRandomMenus(count);
    }
}
