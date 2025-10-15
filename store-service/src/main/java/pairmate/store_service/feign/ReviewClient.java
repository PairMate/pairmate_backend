package pairmate.store_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.dto.ReviewStatsDto;

@FeignClient(name = "review-service")
public interface ReviewClient {
    @GetMapping("/reviews/{reviewId}")
    ReviewResponse getReviewById(@PathVariable("reviewId") Long reviewId);

    @GetMapping("/reviews/internal/stats/store/{storeId}")
    ApiResponse<ReviewStatsDto> getReviewStatsByStoreId(@PathVariable("storeId") Long storeId);
}