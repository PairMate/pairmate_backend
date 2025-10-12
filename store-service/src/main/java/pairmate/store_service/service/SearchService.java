package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pairmate.store_service.repository.StoreRepository;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.dto.SearchResponse;
import pairmate.store_service.dto.StoreResponse;
import pairmate.store_service.dto.MenuResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    public SearchResponse search(String query) {
        // 이름 또는 카테고리로 가게 검색 (리포지토리 메서드 변경됨)
        List<StoreResponse> stores = storeRepository
                .searchStores(query)
                .stream().map(StoreResponse::fromEntity).toList();

        // 메뉴명 검색
        List<MenuResponse> menus = menuRepository
                .findByMenuNameContaining(query)
                .stream().map(MenuResponse::fromEntity).toList();

        // 카테고리 이름 목록 추출
        List<String> categories = stores.stream()
                .map(StoreResponse::getStoreCategoryName)
                .distinct()
                .toList();

        return SearchResponse.builder()
                .stores(stores)
                .menus(menus)
                .storeCategories(categories)
                .build();
    }
}
