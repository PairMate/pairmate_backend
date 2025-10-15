package pairmate.review_service.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.review_service.dto.ReviewRequest;
import pairmate.review_service.dto.ReviewResponse;
import pairmate.review_service.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "음식점 후기 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "음식점 후기 목록 조회", description = "특정 음식점(storeId)에 작성된 모든 후기를 반환합니다.")
    @GetMapping
    public ApiResponse<List<ReviewResponse>> getReviewsByStoreId(@RequestParam Long storeId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByStoreId(storeId);
        return ApiResponse.onSuccess(reviews, SuccessCode.OK);
    }

    @Operation(summary = "음식점 후기 작성", description = "로그인한 유저가 해당 음식점에 후기를 등록합니다.")
    @PostMapping()
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "후기 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "요청한 가게(Store)를 찾을 수 없을 때"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "외부 서비스(store-service) 호출 중 오류 발생 시")
    })
    public ApiResponse<ReviewResponse> createReview(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long storeId,
            @RequestBody ReviewRequest dto) {
        ReviewResponse review = reviewService.createReview(dto, storeId, userId);
        return ApiResponse.onSuccess(review, SuccessCode.CREATED);
    }

    @Operation(summary = "음식점 후기 수정", description = "로그인한 유저가 본인이 작성한 후기를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ApiResponse<Void> updateReview(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest dto) {
        reviewService.updateReview(reviewId, dto, userId);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }

    @Operation(summary = "음식점 후기 삭제", description = "로그인한 유저가 본인이 작성한 후기를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }
}