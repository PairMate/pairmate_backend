package pairmate.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginDTO {
    @NotNull(message = "아이디는 필수입니다.")
    private String loginId;

    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;
}
