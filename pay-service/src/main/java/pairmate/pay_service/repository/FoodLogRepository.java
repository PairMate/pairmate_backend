package pairmate.pay_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.pay_service.domain.Foodlogs;

import java.util.List;

public interface FoodLogRepository extends JpaRepository<Foodlogs, Long> {

    // 최근 티켓 목록 (최대 6개)
    List<Foodlogs> findTop6ByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Foodlogs> findByFoodLogId(Long foodLogId);

}