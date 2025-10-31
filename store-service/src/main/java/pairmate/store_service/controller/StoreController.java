package pairmate.store_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.store_service.service.StoreService;
import pairmate.store_service.dto.MenuResponse;
import pairmate.store_service.dto.StoreRegisterRequest;
import pairmate.store_service.dto.MenuRequest;
import pairmate.store_service.dto.StoreResponse;

import java.util.List;

@RestController
@Controller
@RequestMapping("/stores")
@Tag(name = "Store", description = "식당 관련 API")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final ObjectMapper objectMapper; // JSON 문자열 변환을 위해 주입

    /**
     * review-service 등 다른 서비스와의 내부 통신용 API
     */
    @Hidden     // 스웨거에서 보여줄 필요가 없는 내부 통신용 API라서 숨겼어요
    @GetMapping("/internal/{storeId}")
    public ApiResponse<StoreResponse> getStoreByIdInternal(@PathVariable Long storeId) {
        StoreResponse store = storeService.getStoreByIdInternal(storeId);
        return ApiResponse.onSuccess(store, SuccessCode.OK);
    }

    @Operation(summary = "추천 가게 목록 조회", description = "추천 가게 리스트를 반환합니다. (무료 제공 인원이 많은 순에서 적은 순으로 조회)")
    @GetMapping("/recommend")
    public ApiResponse<List<StoreResponse>> getRecommendedStores() {
        List<StoreResponse> stores = storeService.getRecommendedStores();
        return ApiResponse.onSuccess(stores, SuccessCode.OK);
    }

    @Operation(summary = "가게 상세 조회", description = "가게 세부 사항을 확인합니다.")
    @GetMapping("/{storeId}")
    public ApiResponse<StoreResponse> getStoreDetail(@PathVariable Long storeId) {
        StoreResponse storeDetail = storeService.getStoreDetail(storeId);
        return ApiResponse.onSuccess(storeDetail, SuccessCode.OK);
    }

    @Operation(summary = "가게 메뉴 목록 조회", description = "선택한 가게의 전체 메뉴 리스트를 조회합니다.")
    @GetMapping("/{storeId}/menus")
    public ApiResponse<List<MenuResponse>> getStoreMenus(@PathVariable Long storeId) {
        List<MenuResponse> menus = storeService.getStoreMenus(storeId);
        return ApiResponse.onSuccess(menus, SuccessCode.OK);
    }

    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다.")
    @PostMapping(path = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) // multipart/form-data만 남겨도 됩니다.
//    @SneakyThrows
    public ApiResponse<Long> registerStore(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "storeImage", required = false) MultipartFile storeImage)
            throws JsonProcessingException {

        // ObjectMapper로 String을 DTO 객체로 수동 변환
        StoreRegisterRequest request = objectMapper.readValue(requestJson, StoreRegisterRequest.class);

        Long newStoreId = storeService.registerStore(request, storeImage, userId);
        return ApiResponse.onSuccess(newStoreId, SuccessCode.CREATED);
    }

    @Operation(summary = "가게 수정", description = "기존 가게 정보를 수정합니다.")
    @PutMapping(path = "/{storeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @SneakyThrows
    public ApiResponse<Void> updateStore(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long storeId,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "storeImage", required = false) MultipartFile storeImage) {

        StoreRegisterRequest request = objectMapper.readValue(requestJson, StoreRegisterRequest.class);
        storeService.updateStore(storeId, request, storeImage, userId);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }

}
