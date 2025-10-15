package pairmate.review_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.review_service.domain.ReviewImages;
import pairmate.review_service.domain.Reviews;
import pairmate.review_service.dto.ReviewRequest;
import pairmate.review_service.dto.ReviewResponse;
import pairmate.review_service.feign.StoreClient;
import pairmate.review_service.feign.StoreResponse;
import pairmate.review_service.repository.ReviewImageRepository;
import pairmate.review_service.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final StoreClient storeClient;

    /**
     * 특정 음식점 후기 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByStoreId(Long storeId) {
        StoreResponse store = storeClient.getStoreById(storeId);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        return reviewRepository.findByStoreId(storeId)
                .stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getUserId())
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

    /**
     * 후기 작성
     */
    @Transactional
    public ReviewResponse createReview(ReviewRequest dto, Long storeId, Long userId) {
        StoreResponse store = storeClient.getStoreById(storeId);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        Reviews review = Reviews.builder()
                .storeId(storeId)
                .userId(userId)
                .starRating(dto.getStarRating())
                .visitDate(dto.getVisitDate())
                .content(dto.getContent())
                .build();

        Reviews savedReview = reviewRepository.save(review);

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                reviewImageRepository.save(
                        ReviewImages.builder()
                                .review(savedReview)
                                .reviewImageUrl(dto.getImageUrls().get(i))
                                .sequence(i + 1)
                                .build()
                );
            }
        }
        return new ReviewResponse(savedReview, dto.getImageUrls());
    }

    /**
     * 후기 수정
     */
    @Transactional
    public void updateReview(Long reviewId, ReviewRequest dto, Long userId) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        if (dto.getStarRating() != null) review.setStarRating(dto.getStarRating());
        if (dto.getVisitDate() != null) review.setVisitDate(dto.getVisitDate());
        if (dto.getContent() != null) review.setContent(dto.getContent());
    }

    /**
     * 후기 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 유저ㅓ 권한 확인
        if (!review.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 연관된 이미지를 먼저 삭제 후, 리뷰를 삭제합니다.
        reviewImageRepository.deleteAll(reviewImageRepository.findByReview_ReviewId(reviewId));
        reviewRepository.delete(review);
    }
}