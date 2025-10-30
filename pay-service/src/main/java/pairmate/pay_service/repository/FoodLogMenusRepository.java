package pairmate.pay_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.pay_service.domain.FoodLogMenus;
import pairmate.pay_service.domain.Foodlogs;

import java.util.List;

public interface FoodLogMenusRepository extends JpaRepository<FoodLogMenus, Long> {

  //  List<Foodlogs> findTop6ByUserIdOrderByCreatedAtDesc(Long userId);
}
