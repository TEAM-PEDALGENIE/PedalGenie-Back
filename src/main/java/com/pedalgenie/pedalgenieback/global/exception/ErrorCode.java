package com.pedalgenie.pedalgenieback.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    // 401
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 401, "토큰이 만료되었습니다."),
    NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, 401, "해당 토큰은 유효하지 않습니다."),
    NOT_EXISTS_AUTHORIZATION(HttpStatus.UNAUTHORIZED, 401, "Authorization 헤더가 비어 있습니다."),
    NOT_VALID_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, 401, "인증 타입이 Bearer 타입이 아닙니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, 401, "해당 refresh token은 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 401, "해당 refresh token은 만료되었습니다."),
    NOT_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, 401, "해당 토큰은 ACCESS TOKEN이 아닙니다."),

    // 403
    NO_PERMISSION(HttpStatus.FORBIDDEN, 403, "권한이 없습니다."),
    FORBIDDEN_ROLE(HttpStatus.FORBIDDEN, 403, "허용되지 않은 역할을 가진 유저의 요청입니다."),

    // 404
    NOT_EXISTS_MEMBER_ID(HttpStatus.NOT_FOUND, 404, "존재하지 않는 멤버 아이디입니다."),
    NOT_EXISTS_MEMBER_NICKNAME(HttpStatus.NOT_FOUND, 404, "존재하지 않는 멤버 닉네임입니다."),
    NOT_EXISTS_MEMBER_EMAIL(HttpStatus.NOT_FOUND, 404, "존재하지 않는 멤버 이메일입니다."),

    // 409
    ALREADY_REGISTERED_MEMBER_EMAIL(HttpStatus.CONFLICT, 409, "이미 가입된 이메일입니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
