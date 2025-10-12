package pairmate.review_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.review_service.domain.Reviews;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByStoreId(Long storeId); // storeId 필드가 Reviews 엔티티에 있어야 함
}
