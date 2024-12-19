package cjkimhello97.toy.crashMyServer.redis.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum TokenExceptionInfo implements ExceptionType {

    NO_REFRESH_TOKEN(Status.BAD_REQUEST, 3000, "NO REFRESH TOKEN"),
    NO_ACCESS_TOKEN(Status.BAD_REQUEST, 3001, "NO ACCESS TOKEN"),
    BLACKLISTED_ACCESS_TOKEN(Status.BAD_REQUEST, 3002, "BLACKLISTED ACCESS TOKEN"),
    ;

    private final Status status;
    private final int exceptionCode;
    private final String message;

    TokenExceptionInfo(Status status, int exceptionCode, String message) {
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