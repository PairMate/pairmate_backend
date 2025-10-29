package pairmate.user_service.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import pairmate.user_service.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByLoginId(String loginId);

    Optional<Object> findByNickname(@NotBlank(message = "닉네임은 필수 입력값입니다.") @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.") String nickName);

    Users findByUserId(Long userId);

    Users findByPassword(String password);
}
