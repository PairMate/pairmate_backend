package pairmate.store_service.feign;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {
    private Long reviewId;
    private Long userId;
    private Float starRating;
    private LocalDateTime visitDate;
    private String content;
    private List<String> imageUrls;
}
