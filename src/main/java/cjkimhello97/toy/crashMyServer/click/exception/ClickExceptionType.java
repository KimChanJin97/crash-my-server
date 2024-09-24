package cjkimhello97.toy.crashMyServer.click.exception;

import static cjkimhello97.toy.crashMyServer.common.exception.support.Status.*;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum ClickExceptionType implements ExceptionType {

    CLICK_NOT_FOUND(BAD_REQUEST, 5001, "클릭한 적이 없습니다."),
    ;

    private final Status status;
    private final int exceptionCode;
    private final String message;

    ClickExceptionType(Status status, int exceptionCode, String message) {
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
