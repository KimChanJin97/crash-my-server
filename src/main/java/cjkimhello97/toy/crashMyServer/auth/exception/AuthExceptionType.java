package cjkimhello97.toy.crashMyServer.auth.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum AuthExceptionType implements ExceptionType {

    // JWT
    UNAUTHORIZED(Status.UNAUTHORIZED, 4001, "AUTHORIZATION FAILED"),
    EXPIRED_TOKEN(Status.UNAUTHORIZED, 4002, "TOKEN EXPIRED"),
    INVALID_SIGNATURE(Status.UNAUTHORIZED, 4003, "INVALID SIGNATURE"),
    MALFORMED_TOKEN(Status.UNAUTHORIZED, 4004, "FORGED TOKEN"),
    INVALID_TOKEN(Status.UNAUTHORIZED, 4005, "INVALID TOKEN"),
    MEMBER_NOT_FOUND(Status.NOT_FOUND, 4006, "MEMBER NOT FOUND"),

    // 회원가입
    WRONG_PASSWORD(Status.BAD_REQUEST, 4007, "WRONG PASSWORD"),
    NICKNAME_EXCEED_LENGTH_TEN(Status.BAD_REQUEST, 4008, "NICKNAME EXCEED LENGTH TEN"),
    ALREADY_SIGN_OUT(Status.UNAUTHORIZED, 4009, "ALREADY SIGN OUT")
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
