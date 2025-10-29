package pairmate.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pairmate.user_service.domain.UserType;
import pairmate.user_service.domain.Users;

public class UserDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponseDTO {
        private Long userId;
        private String nickname;
        private String loginId;
        private TokenDTO token;

        public LoginResponseDTO(Users user, TokenDTO token) {
            this.userId = user.getUserId();
            this.nickname = user.getNickname();
            this.loginId = user.getLoginId();
            this.token = token;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponseDTO {
        private Long userId;
        private String userRole;
        private String nickname;
        private String loginId;
        private UserType userType;
        public UserResponseDTO(Users user) {
            this.userId = user.getUserId();
            this.userRole = user.getUserRole();
            this.nickname = user.getNickname();
            this.loginId = user.getLoginId();
            this.userType = user.getUserType();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PasswordRequestDTO {
        private String password;
        public PasswordRequestDTO(String password) {
            this.password = password;
        }
    }

}
