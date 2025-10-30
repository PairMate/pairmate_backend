package pairmate.pay_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.pay_service.domain.Foodlogs;

import java.time.LocalDate;
import java.util.Optional;

public interface FoodLogsRepository extends JpaRepository<Foodlogs, Long> {
    Optional<Integer> sumPriceByUserAndPeriod(Long userId, LocalDate start, LocalDate end);

    Optional<Integer> sumPriceByUserAndDate(Long userId, LocalDate today);
}
