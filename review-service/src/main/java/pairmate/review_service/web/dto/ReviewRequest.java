package pairmate.review_service.web.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewRequest {
    private Float starRating;
    private LocalDateTime visitDate;
    private String content;
    private List<String> imageUrls; // 이미지 여러 개 등록 지원
}
