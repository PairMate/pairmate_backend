package pairmate.review_service.web.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Float starRating;
    private LocalDateTime visitDate;
    private String content;
    private List<String> imageUrls;
}
