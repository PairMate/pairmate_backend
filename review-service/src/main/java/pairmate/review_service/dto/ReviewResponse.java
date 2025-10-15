package pairmate.review_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pairmate.review_service.domain.Reviews;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long userId;
    private Float starRating;
    private LocalDateTime visitDate;
    private String content;
    private List<String> imageUrls;

    public ReviewResponse(Reviews review, List<String> imageUrls) {
        this.reviewId = review.getReviewId();
        this.userId = review.getUserId();
        this.starRating = review.getStarRating();
        this.visitDate = review.getVisitDate();
        this.content = review.getContent();
        this.imageUrls = imageUrls;
    }
}
