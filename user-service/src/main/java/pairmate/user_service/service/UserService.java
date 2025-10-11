package pairmate.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.user_service.domain.RefreshToken;
import pairmate.user_service.domain.Users;
import pairmate.user_service.dto.LoginDTO;
import pairmate.user_service.dto.SignUpDTO;
import pairmate.user_service.dto.TokenDTO;
import pairmate.user_service.dto.UserDTO;
import pairmate.user_service.repository.RefreshTokenRepository;
import pairmate.user_service.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 회원가입
     */
    @Transactional
    public UserDTO.UserResponseDTO signUp(SignUpDTO dto) {
        log.info("[USER] 회원가입 요청 loginId={}", dto.getLoginId());

        // 중복 아이디 체크
        userRepository.findByLoginId(dto.getLoginId())
                .ifPresent(u -> {
                    log.warn("[USER] 회원가입 실패 - 중복된 로그인 ID={}", dto.getLoginId());
                    throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
                });

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        log.debug("[USER] 비밀번호 암호화 완료 loginId={}", dto.getLoginId());

        // 엔티티 변환 및 저장
        Users user = Users.builder()
                .loginId(dto.getLoginId())
                .nickname(dto.getNickName())
                .password(encodedPassword)
                .userRole("USER")
                .build();

        userRepository.save(user);
        log.info("[USER] 회원가입 성공 userId={}, loginId={}", user.getUserId(), user.getLoginId());

        return new UserDTO.UserResponseDTO(user);
    }

    /**
     * 로그인
     */
    @Transactional
    public UserDTO.LoginResponseDTO login(LoginDTO dto) {
        log.info("[AUTH] 로그인 요청 loginId={}", dto.getLoginId());

        Users user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> {
                    log.warn("[AUTH] 로그인 실패 - 존재하지 않는 사용자 loginId={}", dto.getLoginId());
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("[AUTH] 로그인 실패 - 비밀번호 불일치 loginId={}", dto.getLoginId());
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getLoginId());
        String refreshTokenValue = jwtProvider.createRefreshToken(user.getUserId(), user.getLoginId());
        log.debug("[AUTH] 토큰 생성 완료 loginId={}", user.getLoginId());

        // Redis 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(user.getLoginId())
                .userId(user.getUserId())
                .refreshToken(refreshTokenValue)
                .accessToken(accessToken)
                .expiration(LocalDateTime.now().plusDays(3))
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("[AUTH] 로그인 성공 userId={}, loginId={}", user.getUserId(), user.getLoginId());

        return new UserDTO.LoginResponseDTO(user, new TokenDTO(accessToken));
    }

    /**
     * 엑세스 토큰 재발급
     */
    @Transactional
    public TokenDTO reissue(String refreshTokenValue) {
        log.info("[AUTH] 토큰 재발급 요청");

        // 토큰 유효성 검사
        if (!jwtProvider.validateToken(refreshTokenValue)) {
            log.warn("[AUTH] 토큰 재발급 실패 - 유효하지 않은 RefreshToken");
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에서 RefreshToken 검증
        RefreshToken stored = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.warn("[AUTH] 토큰 재발급 실패 - Redis에 RefreshToken 없음");
                    return new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
                });

        // 만료 여부 확인
        if (stored.getExpiration().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(stored.getTokenId());
            log.warn("[AUTH] 토큰 재발급 실패 - RefreshToken 만료 tokenId={}", stored.getTokenId());
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 새 AccessToken 생성
        String newAccessToken = jwtProvider.createAccessToken(stored.getUserId(), stored.getTokenId());
        log.debug("[AUTH] 새 AccessToken 생성 userId={}", stored.getUserId());

        // Redis에 최신 AccessToken 갱신
        RefreshToken updated = RefreshToken.builder()
                .tokenId(stored.getTokenId())
                .userId(stored.getUserId())
                .refreshToken(stored.getRefreshToken())
                .accessToken(newAccessToken)
                .expiration(stored.getExpiration())
                .build();

        refreshTokenRepository.save(updated);
        log.info("[AUTH] 토큰 재발급 성공 userId={}", stored.getUserId());

        return new TokenDTO(newAccessToken);
    }

    @Transactional
    public UserDTO.UserResponseDTO getCurrentUser(Users user) {
        return new UserDTO.UserResponseDTO(user);
    }

}
