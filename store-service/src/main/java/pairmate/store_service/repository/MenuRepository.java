package pairmate.store_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pairmate.store_service.domain.Menus;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menus, Long> {
    List<Menus> findByStoreStoreId(Long storeId);

    @Query(value = "SELECT * FROM menus ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Menus> findRandomMenus(int count);

    List<Menus> findByMenuNameContaining(String menuName);
}
