package pairmate.review_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.review_service.domain.Reviews;
import pairmate.review_service.domain.ReviewImages;
import pairmate.review_service.repository.ReviewRepository;
import pairmate.review_service.repository.ReviewImageRepository;
import pairmate.review_service.web.dto.ReviewRequest;
import pairmate.review_service.web.dto.ReviewResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    public List<ReviewResponse> getReviewsByStoreId(Long storeId) {
        return reviewRepository.findByStoreId(storeId).stream().map(
                review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .starRating(review.getStarRating())
                        .visitDate(review.getVisitDate())
                        .content(review.getContent())
                        .imageUrls(reviewImageRepository.findByReview_ReviewId(review.getReviewId())
                                .stream().map(ReviewImages::getReviewImageUrl).toList())
                        .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public Long createReview(ReviewRequest dto, Long storeId) {
        Reviews review = Reviews.builder()
                // store 연관관계 매핑 필요
                .starRating(dto.getStarRating())
                .visitDate(dto.getVisitDate())
                .content(dto.getContent())
                .build();
        Reviews saved = reviewRepository.save(review);

        if (dto.getImageUrls() != null) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                reviewImageRepository.save(
                        ReviewImages.builder()
                                .review(saved)
                                .reviewImageUrl(dto.getImageUrls().get(i))
                                .order(i + 1)
                                .build()
                );
            }
        }
        return saved.getReviewId();
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewRequest dto) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        // DTO의 각 필드가 null이 아니라면 업데이트 수행 (entity의 update 메서드 내에서도 null 체크 가능)
        if (dto.getStarRating() != null) {
            review.setStarRating(dto.getStarRating());
        }
        if (dto.getVisitDate() != null) {
            review.setVisitDate(dto.getVisitDate());
        }
        if (dto.getContent() != null) {
            review.setContent(dto.getContent());
        }

        reviewRepository.save(review);
    }


    @Transactional
    public void deleteReview(Long reviewId) {
        reviewImageRepository.deleteAll(reviewImageRepository.findByReview_ReviewId(reviewId));
        reviewRepository.deleteById(reviewId);
    }
}
