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
import pairmate.store_service.dto.*;
import pairmate.store_service.feign.ReviewClient;
import pairmate.store_service.feign.UserClient;
import pairmate.store_service.repository.MenuRepository;
import pairmate.store_service.repository.StoreCategoryRepository;
import pairmate.store_service.repository.StoreRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ReviewClient reviewClient;
    private final UserClient userClient;
    private final FileUploadService fileUploadService;

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

        ReviewStatsDto stats;
        try {
            ApiResponse<ReviewStatsDto> response = reviewClient.getReviewStatsByStoreId(store.getStoreId());
            stats = response.getResult();
        } catch (Exception e) {
            stats = new ReviewStatsDto(0.0, 0L);
        }

        return StoreResponse.from(store, stats);
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getStoreMenus(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }
        return menuRepository.findByStoreStoreId(storeId)
                .stream().map(MenuResponse::fromEntity).toList();
    }

    // 가게 등록
    @Transactional
        public Long registerStore(StoreRegisterRequest request, MultipartFile storeImage, Long userId) {

            ApiResponse<UserResponseDto> userResponse;
            try {
                userResponse = userClient.getUserById(userId);
            } catch (Exception e) {

                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회에 실패했습니다.");
            }

            UserResponseDto user = userResponse.getResult();

            if (user == null) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            if (!"ADMIN".equals(user.getUserRole() )) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }

        if (request.getStoreOpenTime() != null && request.getStoreCloseTime() != null &&
                request.getStoreCloseTime().isBefore(request.getStoreOpenTime())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "영업 종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }

        // 이미지 처리
        String imageUrl = null;
        if (storeImage != null && !storeImage.isEmpty()) {
            imageUrl = fileUploadService.uploadFile(storeImage);
        }

        StoreCategories category = storeCategoryRepository.findById(request.getStoreCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        }

        Stores store = Stores.builder()
                .userId(userId)
                .storeCategory(category)
                .storeName(request.getStoreName())
                .storeContactNumber(request.getStoreContactNumber())
                .storeMainImageUrl(imageUrl)
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
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

    // 가게 수정
    @Transactional
    public void updateStore(Long storeId, StoreRegisterRequest request, Long userId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        ApiResponse<UserResponseDto> userResponse;
        try {
            userResponse = userClient.getUserById(userId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회에 실패했습니다.");
        }

        UserResponseDto user = userResponse.getResult();

        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        if (!"ADMIN".equals(user.getUserRole() )) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (request.getStoreOpenTime() != null && request.getStoreCloseTime() != null &&
                request.getStoreCloseTime().isBefore(request.getStoreOpenTime())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "영업 종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }

        StoreCategories category = storeCategoryRepository.findById(request.getStoreCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        store.updateStoreInfo(
                category,
                request.getStoreName(),
                request.getStoreContactNumber(),
                request.getLongitude(),
                request.getLatitude(),
                request.getStoreType(),
                request.getStoreOpenTime(),
                request.getStoreCloseTime(),
                request.getStoreContent(),
                request.getFreePeople()
        );
    }


}