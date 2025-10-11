package pairmate.review_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.review_service.domain.ReviewImages;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImages, Long> {
    List<ReviewImages> findByReview_ReviewId(Long reviewId);
}
