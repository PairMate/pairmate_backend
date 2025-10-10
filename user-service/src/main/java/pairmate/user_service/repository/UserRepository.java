package pairmate.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.user_service.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByLoginId(String loginId);
}
