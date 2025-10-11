package pairmate.review_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.review_service.domain.ReviewImages;
import pairmate.review_service.domain.Reviews;
import pairmate.review_service.feign.StoreClient;
import pairmate.review_service.feign.StoreResponse;
import pairmate.review_service.repository.ReviewImageRepository;
import pairmate.review_service.repository.ReviewRepository;
import pairmate.review_service.web.dto.ReviewRequest;
import pairmate.review_service.web.dto.ReviewResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final StoreClient storeClient; // OpenFeign으로 store-service 연결

    // 특정 음식점 후기 목록 조회
    public List<ReviewResponse> getReviewsByStoreId(Long storeId) {
        // store-service 호출
        StoreResponse store = storeClient.getStoreById(storeId);
        if (store == null) {
            throw new IllegalArgumentException("해당 storeId에 해당하는 음식점이 존재하지 않습니다.");
        }

        // DB에서 리뷰 조회
        return reviewRepository.findByStoreId(storeId)
                .stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .starRating(review.getStarRating())
                        .visitDate(review.getVisitDate())
                        .content(review.getContent())
                        .imageUrls(
                                reviewImageRepository.findByReview_ReviewId(review.getReviewId())
                                        .stream()
                                        .map(ReviewImages::getReviewImageUrl)
                                        .toList()
                        )
                        .build())
                .collect(Collectors.toList());
    }

    // 후기 작성
    @Transactional
    public Long createReview(ReviewRequest dto, Long storeId) {
        // store-service 호출
        StoreResponse store = storeClient.getStoreById(storeId);
        if (store == null) {
            throw new IllegalArgumentException("해당 음식점이 존재하지 않습니다.");
        }

        // 리뷰 생성 및 저장
        Reviews review = Reviews.builder()
                // storeId는 단순 참조 (실제 연관관계 매핑은 review 엔티티에서 storeId 필드로 관리)
                .storeId(storeId)
                .starRating(dto.getStarRating())
                .visitDate(dto.getVisitDate())
                .content(dto.getContent())
                .build();

        Reviews saved = reviewRepository.save(review);

        // 리뷰 이미지 저장
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
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

    // 후기 수정
    @Transactional
    public void updateReview(Long reviewId, ReviewRequest dto) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        // DTO의 각 필드가 null이 아니면 업데이트
        if (dto.getStarRating() != null) review.setStarRating(dto.getStarRating());
        if (dto.getVisitDate() != null) review.setVisitDate(dto.getVisitDate());
        if (dto.getContent() != null) review.setContent(dto.getContent());

        reviewRepository.save(review);
    }

    // 후기 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewImageRepository.deleteAll(reviewImageRepository.findByReview_ReviewId(reviewId));
        reviewRepository.deleteById(reviewId);
    }
}
