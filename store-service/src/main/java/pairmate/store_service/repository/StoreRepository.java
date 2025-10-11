package pairmate.store_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pairmate.store_service.domain.Stores;

import java.util.List;

public interface StoreRepository extends JpaRepository<Stores, Long> {
    // 추천 기준은 상황에 따라 수정
    @Query("SELECT s FROM Stores s WHERE s.freePeople >= 4")
    List<Stores> findRecommended();

    List<Stores> findByStoreNameContainingOrStoreCategory_StoreCategoryNameContaining(String name, String catName);
}
