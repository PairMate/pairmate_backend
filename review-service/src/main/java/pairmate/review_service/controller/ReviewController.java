package pairmate.review_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.review_service.dto.ReviewRequest;
import pairmate.review_service.dto.ReviewResponse;
import pairmate.common_libs.dto.ReviewStatsDto;
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
    private final ObjectMapper objectMapper;

    /**
     * 가게 리뷰 통계 조회 (내부 통신용)
     */
    @Hidden // 서비스 간 통신용이므로 Swagger 문서에서 숨깁니다.
    @Operation(summary = "가게 리뷰 통계 조회 (내부 통신용)")
    @GetMapping("/internal/stats/store/{storeId}")
    public ApiResponse<ReviewStatsDto> getReviewStats(@PathVariable Long storeId) {
        ReviewStatsDto stats = reviewService.getReviewStatsByStoreId(storeId);
        return ApiResponse.onSuccess(stats, SuccessCode.OK);
    }

    @Operation(summary = "음식점 후기 목록 조회", description = "특정 음식점(storeId)에 작성된 모든 후기를 반환합니다.")
    @GetMapping(path = "/{storeId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByStoreId(@PathVariable Long storeId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByStoreId(storeId);
        return ApiResponse.onSuccess(reviews, SuccessCode.OK);
    }

    @Operation(summary = "음식점 후기 작성", description = "로그인한 유저가 해당 음식점에 후기를 등록합니다.")
    @PostMapping(path = "/{storeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "후기 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "요청한 가게(Store)를 찾을 수 없을 때"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "외부 서비스(store-service) 호출 중 오류 발생 시")
    })
    public ApiResponse<ReviewResponse> createReview(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long storeId,
            @RequestPart("request") String requestJson, // 리뷰 내용 (JSON 문자열)
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws JsonProcessingException {
        // JSON 문자열을 DTO 객체로 변환
        ReviewRequest dto = objectMapper.readValue(requestJson, ReviewRequest.class);

        ReviewResponse review = reviewService.createReview(dto, storeId, userId, images);
        return ApiResponse.onSuccess(review, SuccessCode.CREATED);
    }

    /**
     * api 과잉 이슈...로 안 쓰는 api 주석처리합니다.
     */
//    @Operation(summary = "음식점 후기 수정", description = "로그인한 유저가 본인이 작성한 후기를 수정합니다.")
//    @PutMapping("/{reviewId}")
//    public ApiResponse<Void> updateReview(
//            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
//            @PathVariable Long reviewId,
//            @RequestBody ReviewRequest dto) {
//        reviewService.updateReview(reviewId, dto, userId);
//        return ApiResponse.onSuccess(null, SuccessCode.OK);
//    }
//
//    @Operation(summary = "음식점 후기 삭제", description = "로그인한 유저가 본인이 작성한 후기를 삭제합니다.")
//    @DeleteMapping("/{reviewId}")
//    public ApiResponse<Void> deleteReview(
//            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
//            @PathVariable Long reviewId) {
//        reviewService.deleteReview(reviewId, userId);
//        return ApiResponse.onSuccess(null, SuccessCode.OK);
//    }
}