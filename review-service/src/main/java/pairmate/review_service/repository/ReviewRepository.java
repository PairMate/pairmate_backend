package pairmate.review_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pairmate.review_service.domain.Reviews;
import pairmate.common_libs.dto.ReviewStatsDto;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByStoreId(Long storeId); // storeId 필드가 Reviews 엔티티에 있어야 함

    // storeId를 기반으로 평균 별점과 리뷰 개수
    @Query("SELECT new pairmate.common_libs.dto.ReviewStatsDto(COALESCE(AVG(r.starRating), 0.0), COUNT(r)) " +
            "FROM Reviews r WHERE r.storeId = :storeId")
    ReviewStatsDto findReviewStatsByStoreId(@Param("storeId") Long storeId);
}