package pairmate.user_service.controller;

import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.common_libs.response.SuccessCode;
import pairmate.user_service.domain.Users;
import pairmate.user_service.dto.LoginDTO;
import pairmate.user_service.dto.SignUpDTO;
import pairmate.user_service.dto.TokenDTO;
import pairmate.user_service.dto.UserDTO;
import pairmate.user_service.service.UserService;


@Tag(name = "User API", description = "유저 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     *  회원가입
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입을 진행")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 로그인 ID")
    })
    public ApiResponse<UserDTO.UserResponseDTO> signUp(@Valid @RequestBody SignUpDTO dto) {
         userService.signUp(dto);
        return ApiResponse.onSuccess(
                userService.signUp(dto), SuccessCode.CREATED
        );
    }

    /**
     *  로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 후 AccessToken과 RefreshToken을 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = UserDTO.LoginResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "LOGIN_FAILED - 아이디 또는 비밀번호 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "USER_NOT_FOUND - 사용자를 찾을 수 없음")
    })
    public ApiResponse<UserDTO.LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        UserDTO.LoginResponseDTO response = userService.login(dto);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }

    /**
     *  AccessToken 재발급
     */
    @PostMapping("/reissue")
    @Operation(summary = "AccessToken 재발급", description = "새로운 AccessToken을 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "INVALID_REFRESH_TOKEN - 유효하지 않거나 만료된 RefreshToken")
    })
    public ApiResponse<TokenDTO> reissue(@RequestHeader("Authorization") String authorizationHeader) {
        String refreshToken = extractTokenFromHeader(authorizationHeader);
        TokenDTO response = userService.reissue(refreshToken);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }

    /**
     * Authorization 헤더에서 "Bearer " 제거
     */
    private String extractTokenFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return header.substring(7);
    }
}
