package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.store_service.domain.Menus;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.web.dto.MenuResponse;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getRandomMenus(int count) {
        List<Menus> allMenus = menuRepository.findAll();
        // 단순 랜덤 추출 (실서비스라면 쿼리로 처리하는 것이 성능상 더 적합)
        return new Random().ints(0, allMenus.size())
                .distinct()
                .limit(count)
                .mapToObj(allMenus::get)
                .map(MenuResponse::fromEntity)
                .toList();
    }
}
