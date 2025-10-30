package pairmate.pay_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.pay_service.domain.FoodLogMenus;

public interface FoodLogMenusRepository extends JpaRepository<FoodLogMenus, Long> {
}
