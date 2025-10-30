package pairmate.pay_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.pay_service.domain.ChildCards;

import java.util.List;
import java.util.Optional;

public interface ChildCardsRepository extends JpaRepository<ChildCards, Long> {
    boolean existsByCardNum(String cardNum);
    Optional<ChildCards> findByUserId(Long userId);
    List<ChildCards> findAllByUserId(Long userId);
    Optional<ChildCards> findByCardId(Long cardId);

    Optional<ChildCards> findTop1ByUserIdOrderByCardIdDesc(Long userId);
}