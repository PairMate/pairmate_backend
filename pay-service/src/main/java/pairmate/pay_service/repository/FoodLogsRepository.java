package pairmate.pay_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pairmate.pay_service.domain.Foodlogs;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FoodLogsRepository extends JpaRepository<Foodlogs, Long> {

    // 최근 티켓 목록 (최대 6개)
    List<Foodlogs> findTop6ByUserIdOrderByCreatedAtDesc(Long userId);

}