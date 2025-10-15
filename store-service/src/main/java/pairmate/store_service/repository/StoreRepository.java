package pairmate.store_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pairmate.store_service.domain.Stores;

import java.util.List;

public interface StoreRepository extends JpaRepository<Stores, Long> {
    // freePeople이 많은 순서로 모든 가게를 정렬하여 조회
    @Query("SELECT s FROM Stores s ORDER BY s.freePeople DESC")
    List<Stores> findRecommended();

   @Query("SELECT s FROM Stores s " +
            "WHERE s.storeName LIKE %:keyword% " +
            "OR s.storeCategory.storeCategoryName LIKE %:keyword%")
    List<Stores> searchStores(@Param("keyword") String keyword);

}
