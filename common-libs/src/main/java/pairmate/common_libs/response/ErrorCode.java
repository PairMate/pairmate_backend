package pairmate.common_libs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "서버 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "요청 파라미터가 올바르지 않습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "DATE-400", "유효하지 않은 날짜입니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "FORMAT-400", "형식이 올바르지 않습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "COMMON-400", "JSON 파싱에 실패했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "요청한 리소스를 찾을 수 없습니다."),

    // AUTH 에러
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH-401", "인증 토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401", "유효하지 않은 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401", "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH-401", "Refresh Token이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401", "유효하지 않은 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401", "Refresh Token이 만료되었습니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-401", "토큰 서명이 올바르지 않습니다."),
    TOKEN_PARSING_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-401", "토큰 파싱 중 오류가 발생했습니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401", "지원하지 않는 토큰 형식입니다."),
    AUTH_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "AUTH-401", "Authorization 헤더가 누락되었습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-401", "해당 사용자를 찾을 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-401", "로그인에 실패했습니다."),
    TOKEN_REISSUE_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-401", "토큰 재발급에 실패했습니다."),
    ALREADY_LOGGED_OUT(HttpStatus.UNAUTHORIZED, "AUTH-401", "이미 로그아웃된 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH-403", "요청에 대한 권한이 없습니다."),


    // USER 에러
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "USER-409", "이미 존재하는 로그인 ID입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER-409", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER-400", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "USER-400", "닉네임 형식이 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER-400", "비밀번호가 일치하지 않습니다."),

    // 가게 에러
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE-404", "해당 음식점이 존재하지 않습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-404", "해당 카테고리를 찾을 수 없습니다."),


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatusCode getHttpStatus() {
        return HttpStatusCode.valueOf(httpStatus.value());
    }
}