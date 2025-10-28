package pairmate.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pairmate.user_service.domain.UserType;
import pairmate.user_service.domain.Users;

@Getter
@AllArgsConstructor
@NoArgsConstructor
//@Schema(description = "회원가입 요청 DTO")
public class SignUpDTO {

    //@Schema(description = "로그인 ID", example = "jiwon123")
    @NotBlank(message = "로그인 ID는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "로그인 ID는 4~20자여야 합니다.")
    private String loginId;

    //@Schema(description = "닉네임", example = "지원이")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
    private String nickName;

    //@Schema(description = "비밀번호", example = "abcd1234!")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다."
    )
    private String password;

    @NotNull(message = "사용자 유형은 필수 입력값입니다.")
    private UserType userType;
}