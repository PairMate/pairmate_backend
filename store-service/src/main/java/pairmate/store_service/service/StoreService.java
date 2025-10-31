package pairmate.store_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;
import pairmate.pay_service.dto.StoreDTO.MenuResponse;
import pairmate.pay_service.dto.StoreDTO.StoreResponse;
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
            } catch (feign.FeignException.NotFound e) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
            } catch (feign.FeignException e) {
                // 4xx/5xx 중 NotFound 제외한 Feign 오류
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 서비스 오류: " + e.status());
            } catch (Exception e) {
                // 네트워크/타임아웃/직렬화 등
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회에 실패했습니다.");
            }

            UserResponseDto user = userResponse.getResult();

            if (user == null) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            if (!"CEO".equals(user.getUserType() )) {
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
    public void updateStore(Long storeId, StoreRegisterRequest request, MultipartFile storeImage, Long userId) {
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        ApiResponse<UserResponseDto> userResponse;
        try {
            userResponse = userClient.getUserById(userId);
        } catch (feign.FeignException.NotFound e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        } catch (feign.FeignException e) {
            // 4xx/5xx 중 NotFound 제외한 Feign 오류
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 서비스 오류: " + e.status());
        } catch (Exception e) {
            // 네트워크/타임아웃/직렬화 등
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회에 실패했습니다.");
        }

        UserResponseDto user = userResponse.getResult();

        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        if (!"CEO".equals(user.getUserType() )) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (request.getStoreOpenTime() != null && request.getStoreCloseTime() != null &&
                request.getStoreCloseTime().isBefore(request.getStoreOpenTime())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "영업 종료 시간은 시작 시간보다 빠를 수 없습니다.");
        }

        StoreCategories category = storeCategoryRepository.findById(request.getStoreCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 이미지 처리(업데이트 시 새 이미지가 들어오면 교체)
        String imageUrl = null;
        if (storeImage != null && !storeImage.isEmpty()) {
            imageUrl = fileUploadService.uploadFile(storeImage);
        }

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
                request.getFreePeople(),
                imageUrl
        );
    }

    // 내가 등록한 가게 상세정보 조회
    @Transactional(readOnly = true)
    public StoreResponse getMyStoreDetail(Long storeId, Long userId) {
        // 가게 존재 여부와 소유자 확인
        Stores store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
        
        // 요청한 사용자가 가게 소유자인지 확인
        if (!store.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "본인이 등록한 가게만 조회할 수 있습니다.");
        }

        ReviewStatsDto stats;
        try {
            ApiResponse<ReviewStatsDto> response = reviewClient.getReviewStatsByStoreId(store.getStoreId());
            stats = response.getResult();
        } catch (Exception e) {
            stats = new ReviewStatsDto(0.0, 0L);
        }

        return StoreResponse.from(store, stats);
    }

    /**
     * 사용자 ID로 해당 사용자의 가게 ID를 조회합니다.
     * @param userId 사용자 ID
     * @return 가게 ID
     * @throws CustomException 가게를 찾을 수 없는 경우 STORE_NOT_FOUND 예외 발생
     */
    @Transactional(readOnly = true)
    public Long getStoreIdByUserId(Long userId) {
        return storeRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND, "해당 사용자의 가게를 찾을 수 없습니다."))
                .getStoreId();
    }
}
