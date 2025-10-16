package pairmate.pay_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.pay_service.dto.CategoryItemsResponse;
import pairmate.pay_service.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "편의점 품목 관련 API")
public class ItemController {

    private final ItemService itemService;

    /**
     * 카테고리별 아이템 목록 조회 API
     */
    @GetMapping()
    @Operation(summary = "편의점에서 구매 가능한 품목 목록 조회")
    public ApiResponse<List<CategoryItemsResponse>> getAllItems() {
        List<CategoryItemsResponse> items = itemService.getItemsGroupedByCategory();
        return ApiResponse.onSuccess(items, SuccessCode.OK);
    }
}