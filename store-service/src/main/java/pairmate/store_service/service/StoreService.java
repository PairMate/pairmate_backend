package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;
import pairmate.common_libs.dto.ReviewStatsDto;
import pairmate.store_service.domain.Menus;
import pairmate.store_service.domain.StoreCategories;
import pairmate.store_service.domain.Stores;
import pairmate.store_service.dto.MenuRequest;
import pairmate.store_service.dto.MenuResponse;
import pairmate.store_service.dto.StoreRegisterRequest;
import pairmate.store_service.dto.StoreResponse;
import pairmate.store_service.feign.ReviewClient;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.repository.StoreCategoryRepository;
import pairmate.store_service.repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ReviewClient reviewClient;
    // private final S3UploadService s3UploadService;           // 안 쓰는 거 같

    @Transactional(readOnly = true)
    public StoreResponse getStoreByIdInternal(Long storeId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.fromEntity(store);
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> getRecommendedStores() {
        List<Stores> stores = storeRepository.findRecommended();

        return stores.stream().map(store -> {
            ReviewStatsDto stats;
            try {
                ApiResponse<ReviewStatsDto> response = reviewClient.getReviewStatsByStoreId(store.getStoreId());
                stats = response.getResult();
            } catch (Exception e) {
                stats = new ReviewStatsDto(0.0, 0L);
            }

            return StoreResponse.from(store, stats);
        }).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public StoreResponse getStoreDetail(Long storeId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.fromEntity(store);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getStoreMenus(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }
        return menuRepository.findByStoreStoreId(storeId)
                .stream().map(MenuResponse::fromEntity).toList();
    }

    @Transactional
    public Long registerStore(StoreRegisterRequest request, MultipartFile storeImage, Long userId) {
        String imageUrl = null;
        if (storeImage != null && !storeImage.isEmpty()) {
            imageUrl = "https://storeImages/" + storeImage.getOriginalFilename(); // 임시 URL
        }

        StoreCategories category = storeCategoryRepository.findById(request.getStoreCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Stores store = Stores.builder()
                .userId(userId)
                .storeCategory(category)
                .storeName(request.getStoreName())
                .storeContactNumber(request.getStoreContactNumber())
                .storeMainImageUrl(imageUrl)
                .storeLocate(request.getStoreLocate())
                .storeType(request.getStoreType())
                .storeOpenTime(request.getStoreOpenTime())
                .storeCloseTime(request.getStoreCloseTime())
                .storeContent(request.getStoreContent())
                .freePeople(request.getFreePeople())
                .build();
        Stores savedStore = storeRepository.save(store);

        if (request.getMenus() != null && !request.getMenus().isEmpty()) {
            List<Menus> menus = request.getMenus().stream()
                    .map(menuReq -> Menus.builder()
                            .store(savedStore)
                            .menuName(menuReq.getMenuName())
                            .menuPrice(menuReq.getMenuPrice())
                            .build())
                    .toList();
            menuRepository.saveAll(menus);
        }
        return savedStore.getStoreId();
    }

}