package pairmate.store_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.store_service.domain.StoreCategories;

public interface StoreCategoryRepository extends JpaRepository<StoreCategories, Long> {

}
