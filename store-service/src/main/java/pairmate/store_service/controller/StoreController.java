package pairmate.store_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.store_service.domain.Stores;
import pairmate.store_service.repository.StoreRepository;
import pairmate.store_service.service.StoreService;
import pairmate.store_service.dto.MenuResponse;
import pairmate.store_service.dto.StoreRegisterRequest;
import pairmate.store_service.dto.StoreResponse;

import java.util.List;

@Controller
@RestController
@RequestMapping("/stores")
@Tag(name = "Store", description = "식당 관련 API")    // 아직 스웨거 설정을 안해서 일단 tag만 미리 달아두겠습니다
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreRepository storeRepository;


    // review-service에서 storeId 사용하려면 요러케 해야해요
    // 원래 internal을 안 붙이려고 했는데 그러면 아래에 가게 상세 조회 부분이랑 엔드포인트가 겹쳐서 수정합니다.
    @GetMapping("/internal/{storeId}")
    public ResponseEntity<Stores> getStoreById(@PathVariable Long storeId) {
        return storeRepository.findById(storeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 추천 가게 목록 조회
    @Operation(
            summary = "추천 가게 목록 조회",
            description = "추천 가게 리스트를 쭉 보여주는 API 입니다.")
    @GetMapping("/recommend")
    public List<StoreResponse> getRecommendedStores() {
        return storeService.getRecommendedStores();
    }

    // 가게 상세 조회
    @GetMapping("/{storeId}")
    @Operation(summary = "가게 상세 조회", description = "가게 세부 사항을 확인합니다.")
    public StoreResponse getStoreDetail(@PathVariable Long storeId) {
        return storeService.getStoreDetail(storeId);
    }

    // 가게의 메뉴 목록 조회
    @Operation(
            summary = "가게 메뉴 목록 조회",
            description = "선택한 가게에 등록된 전체 메뉴 리스트와 각 메뉴의 정보(이름, 가격 등)를 조회합니다."
    )
    @GetMapping("/{storeId}/menus")
    public List<MenuResponse> getStoreMenus(@PathVariable Long storeId) {
        return storeService.getStoreMenus(storeId);
    }

    // 가게 등록
    @Operation(
            summary = "가게 등록",
            description = "새로운 가게를 등록하는 API입니다."
    )
    @PostMapping
    public ResponseEntity<Void> registerStore(@RequestBody StoreRegisterRequest request) {
        storeService.registerStore(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Authorization 헤더에서 "Bearer " 제거
     */
    private String extractTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return header.substring(7);
    }


}