package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.store_service.domain.Menus;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.dto.MenuResponse;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getRandomMenus(int count) {
        return menuRepository.findRandomMenus(count)
                .stream().map(MenuResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long menuId) {
        Optional<Menus> menu = menuRepository.findByMenuId(menuId);
        if (menu.isPresent()) return MenuResponse.fromEntity(menu.get());
        else { throw new CustomException(ErrorCode.MENU_NOT_EXIST); }
    }
}
