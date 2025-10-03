package pairmate.review_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pairmate.review_service.service.ReviewService;
import pairmate.review_service.web.dto.ReviewRequest;
import pairmate.review_service.web.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "음식점 후기 관련 API")
public class ReviewController {
    private final ReviewService reviewService;
    // private final UserService userService; // 로그인 유저 조회용 서비스

    @Operation(summary = "음식점 후기 목록 조회", description = "특정 음식점(storeId)에 작성된 모든 후기를 반환합니다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(
            HttpServletRequest request,
            @PathVariable Long storeId) {
        // User user = userService.getLoginUser(request);
        // return ResponseEntity.ok(reviewService.getReviewsByStoreId(storeId, user));
        return ResponseEntity.ok(reviewService.getReviewsByStoreId(storeId));
    }

    @Operation(summary = "음식점 후기 작성", description = "로그인한 유저가 해당 음식점에 후기를 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> createReview(
            HttpServletRequest request,
            @RequestParam Long storeId,
            @RequestBody ReviewRequest dto) {
        // User user = userService.getLoginUser(request);
        // Long reviewId = reviewService.createReview(storeId, user, dto);
        Long reviewId = reviewService.createReview(dto, storeId);
        return ResponseEntity.ok(reviewId);
    }

    @Operation(summary = "음식점 후기 수정", description = "로그인한 유저가 본인이 작성한 후기를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            HttpServletRequest request,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest dto) {
        // User user = userService.getLoginUser(request);
        // reviewService.updateReview(reviewId, user, dto);
        reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "음식점 후기 삭제",
            description = "로그인한 유저가 본인이 작성한 후기를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            HttpServletRequest request,
            @PathVariable Long reviewId) {
        // User user = userService.getLoginUser(request);
        // reviewService.deleteReview(reviewId, user);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
