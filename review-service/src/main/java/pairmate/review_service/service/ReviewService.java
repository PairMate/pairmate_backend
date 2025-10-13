package pairmate.review_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            throw new IllegalArgumentException("해당 storeId에 해당하는 음식점이 존재하지 않습니다.");
        }

        return reviewRepository.findByStoreId(storeId)
                .stream()
                .map(review -> ReviewResponse.builder()
                        .reviewId(review.getReviewId())
                        .userId(review.getUserId()) // [수정] 응답에 userId 추가
                        // .userNickname(review.getUserNickname()) // 닉네임도 저장했다면 추가
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
    public Long createReview(ReviewRequest dto, Long storeId, Long userId) {
        StoreResponse store = storeClient.getStoreById(storeId);
        if (store == null) {
            throw new IllegalArgumentException("해당 음식점이 존재하지 않습니다.");
        }

        Reviews review = Reviews.builder()
                .storeId(storeId)
                .userId(userId) // [수정] userId를 리뷰 엔티티에 저장
                .starRating(dto.getStarRating())
                .visitDate(dto.getVisitDate())
                .content(dto.getContent())
                .build();

        Reviews saved = reviewRepository.save(review);

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

    /**
     * 후기 수정
     */
    @Transactional
    public void updateReview(Long reviewId, ReviewRequest dto, Long userId) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        // [수정] 권한 확인 로직 추가
        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        if (dto.getStarRating() != null) review.setStarRating(dto.getStarRating());
        if (dto.getVisitDate() != null) review.setVisitDate(dto.getVisitDate());
        if (dto.getContent() != null) review.setContent(dto.getContent());

        // @Transactional 어노테이션으로 인해 'Dirty Checking'이 동작하므로,
        // 명시적으로 save를 호출하지 않아도 변경 사항이 DB에 반영됩니다.
    }

    /**
     * 후기 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

        // 유저ㅓ 권한 확인
        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        // 연관된 이미지를 먼저 삭제 후, 리뷰를 삭제합니다.
        reviewImageRepository.deleteAll(reviewImageRepository.findByReview_ReviewId(reviewId));
        reviewRepository.delete(review); // deleteById 대신 조회한 엔티티로 삭제
    }
}