package pairmate.pay_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pairmate.pay_service.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}