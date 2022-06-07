package com.example.websocketdemo.exception;

import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_EMAIL(BAD_REQUEST, "유효하지 않은 email 입니다."),
    INVALID_PASSWORD(BAD_REQUEST, "유효하지 않은 password 입니다."),
    INVALID_ID(BAD_REQUEST, "유효하지 않은 id 입니다."),
    INVALID_TOKEN(BAD_REQUEST, "유효하지 않은 token 입니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "유효하지 않은 refresh token 입니다."),
    INVALID_CHAT_ROOM_NAME(BAD_REQUEST, "유효하지 않은 채팅방 이름입니다."),
    /* 401 Unauthorized : 승인되지 않음 */
    UNAUTHENTICATED_USER(UNAUTHORIZED, "로그인 정보가 올바르지 않습니다."),
    /* 403 FORBIDDEN  : 접근 권한이 없음 */
    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    NOT_FOUND_REFRESH_TOKEN(NOT_FOUND, "refresh token 을 찾을 수 없습니다."),
    NOT_FOUND_USER(NOT_FOUND, "유저를 찾을 수 없습니다."),
    NOT_FOUND_CHAT_ROOM(NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_ACCOUNT(CONFLICT, "이미 가입된 계정이 있습니다.");

    private final HttpStatus httpStatus;
    private final String detail;

    ErrorCode(HttpStatus httpStatus, String detail) {
        this.httpStatus = httpStatus;
        this.detail = detail;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDetail() {
        return detail;
    }
}
