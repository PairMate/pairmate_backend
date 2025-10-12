package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.store_service.domain.Stores;
import pairmate.store_service.domain.Menus;
import pairmate.store_service.domain.StoreCategories;
import pairmate.store_service.repository.StoreCategoryRepository;
import pairmate.store_service.repository.StoreRepository;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.dto.StoreRegisterRequest;
import pairmate.store_service.dto.StoreResponse;
import pairmate.store_service.dto.MenuResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final StoreCategoryRepository storeCategoryRepository;

    @Transactional(readOnly = true)
    public List<StoreResponse> getRecommendedStores() {
        return storeRepository.findRecommended()
                .stream().map(StoreResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public StoreResponse getStoreDetail(Long storeId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
        return StoreResponse.fromEntity(store);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getStoreMenus(Long storeId) {
        return menuRepository.findByStore_StoreId(storeId)
                .stream().map(MenuResponse::fromEntity).toList();
    }

    @Transactional
    public void registerStore(StoreRegisterRequest request) {
        StoreCategories category = storeCategoryRepository.findById(request.getStoreCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

        Stores store = Stores.builder()
                .storeCategory(category)
                .storeName(request.getStoreName())
                .storeContactNumber(request.getStoreContactNumber())
                .storeMainImageUrl(request.getStoreMainImageUrl())
                .storeLocate(request.getStoreLocate())
                .storeType(request.getStoreType())
                .storeOpenTime(request.getStoreOpenTime())
                .storeCloseTime(request.getStoreCloseTime())
                .storeContent(request.getStoreContent())
                .freePeople(request.getFreePeople())
                .build();

        storeRepository.save(store);

        // 메뉴 등록하기
        if (request.getMenus() != null && !request.getMenus().isEmpty()) {
            List<Menus> menus = request.getMenus().stream()
                    .map(menuReq -> Menus.builder()
                            .store(store)
                            .menuName(menuReq.getMenuName())
                            .menuPrice(menuReq.getMenuPrice())
                            .build())
                    .toList();
            menuRepository.saveAll(menus);
        }
    }

}
