package pairmate.review_service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.review_service.domain.ReviewImages;
import pairmate.review_service.domain.Reviews;
import pairmate.review_service.dto.ReviewRequest;
import pairmate.review_service.dto.ReviewResponse;
import pairmate.common_libs.dto.ReviewStatsDto;
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

    private final FileUploadService fileUploadService;

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
    public ReviewResponse createReview(ReviewRequest dto, Long storeId, Long userId, List<MultipartFile> images) {
        try {
            StoreResponse store = storeClient.getStoreById(storeId);
            if (store == null) {
                throw new CustomException(ErrorCode.STORE_NOT_FOUND);
            }
        } catch (feign.FeignException.NotFound e) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND, "가게를 찾을 수 없습니다.");
        } catch (feign.FeignException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "가게 서비스 오류: " + e.status());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "가게 정보 조회에 실패했습니다.");
        }

        Reviews review = Reviews.builder()
                .storeId(storeId)
                .userId(userId)
                .starRating(dto.getStarRating())
                .visitDate(dto.getVisitDate())
                .content(dto.getContent())
                .build();

        Reviews savedReview = reviewRepository.save(review);

        List<String> savedImageUrls = new ArrayList<>();
        List<ReviewImages> reviewImagesToSave = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            int sequence = 1;
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imageUrl = fileUploadService.uploadFile(image);
                    savedImageUrls.add(imageUrl);

                    reviewImagesToSave.add(
                            ReviewImages.builder()
                                    .review(savedReview)
                                    .reviewImageUrl(imageUrl)
                                    .sequence(sequence++)
                                    .build()
                    );
                }
            }
        }

        if (!reviewImagesToSave.isEmpty()) {
            reviewImageRepository.saveAll(reviewImagesToSave);
        }

        return new ReviewResponse(savedReview, savedImageUrls);
    }

//    /**
//     * 후기 수정
//     */
//    @Transactional
//    public void updateReview(Long reviewId, ReviewRequest dto, Long userId) {
//        Reviews review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
//
//        if (!review.getUserId().equals(userId)) {
//            // 작성자가 아닐 때
//            throw new CustomException(ErrorCode.FORBIDDEN);
//        }
//
//        if (dto.getStarRating() != null) review.setStarRating(dto.getStarRating());
//        if (dto.getVisitDate() != null) review.setVisitDate(dto.getVisitDate());
//        if (dto.getContent() != null) review.setContent(dto.getContent());
//    }
//
//    /**
//     * 후기 삭제
//     */
//    @Transactional
//    public void deleteReview(Long reviewId, Long userId) {
//        Reviews review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
//
//        // 유저ㅓ 권한 확인
//        if (!review.getUserId().equals(userId)) {
//            throw new CustomException(ErrorCode.FORBIDDEN);
//        }
//
//        // 연관된 이미지를 먼저 삭제 후, 리뷰를 삭제합니다.
//        reviewImageRepository.deleteAll(reviewImageRepository.findByReview_ReviewId(reviewId));
//        reviewRepository.delete(review);
//    }
//
    @Transactional(readOnly = true)
    public ReviewStatsDto getReviewStatsByStoreId(Long storeId) {
        return reviewRepository.findReviewStatsByStoreId(storeId);
    }
}