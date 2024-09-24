package cjkimhello97.toy.crashMyServer.auth.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum AuthExceptionType implements ExceptionType {

    // JWT
    UNAUTHORIZED(Status.UNAUTHORIZED, 4001, "인가에 실패했습니다."),
    EXPIRED_TOKEN(Status.UNAUTHORIZED, 4002, "토큰이 만료되었습니다."),
    INVALID_SIGNATURE(Status.UNAUTHORIZED, 4003, "유효하지 않은 서명입니다."),
    MALFORMED_TOKEN(Status.UNAUTHORIZED, 4004, "토큰이 위조되었습니다."),
    INVALID_TOKEN(Status.UNAUTHORIZED, 4005, "지원하는 토큰 형식이 아닙니다."),
    MEMBER_NOT_FOUND(Status.NOT_FOUND, 4006, "계정이 존재하지 않습니다."),

    // 회원가입
    WRONG_PASSWORD(Status.BAD_REQUEST, 4007, "비밀번호를 잘못 입력하셨습니다."),
    ;

    private final Status status;
    private final int exceptionCode;
    private final String message;

    AuthExceptionType(Status status, int exceptionCode, String message) {
        this.status = status;
        this.exceptionCode = exceptionCode;
        this.message = message;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public int exceptionCode() {
        return exceptionCode;
    }

    @Override
    public String message() {
        return message;
    }
}
