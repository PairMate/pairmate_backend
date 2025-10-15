package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.dto.MenuResponse;

import java.util.List;

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

}
