package pairmate.user_service.dto;

import pairmate.user_service.domain.Users;

public class UserDTO {

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


    public static class UserResponseDTO {
        private Long userId;
        private String nickname;
        private String loginId;
        public UserResponseDTO(Users user) {
            this.userId = user.getUserId();
            this.nickname = user.getNickname();
            this.loginId = user.getLoginId();
        }
    }


}
