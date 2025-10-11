package pairmate.store_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pairmate.store_service.service.StoreService;
import pairmate.store_service.web.dto.MenuResponse;
import pairmate.store_service.web.dto.StoreRegisterRequest;
import pairmate.store_service.web.dto.StoreResponse;

import java.util.List;

@Controller
@RestController
@RequestMapping("/stores")
@Tag(name = "Stores", description = "식당 관련 API")    // 아직 스웨거 설정을 안해서 일단 tag만 미리 달아두겠음
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

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
    @Operation(
            summary = "가게 상세 조회",
            description = "가게 세부 사항들을 확인할 수 있는 API 입니다.")
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

}