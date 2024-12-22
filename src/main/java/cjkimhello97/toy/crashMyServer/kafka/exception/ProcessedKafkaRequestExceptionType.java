package cjkimhello97.toy.crashMyServer.kafka.exception;

import static cjkimhello97.toy.crashMyServer.common.exception.support.Status.SERVER_ERROR;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum ProcessedKafkaRequestExceptionType implements ExceptionType {

    ALREADY_PROCESSED_MESSAGE(SERVER_ERROR, 4000, ""),
    ;


    private final Status status;
    private final int exceptionCode;
    private final String message;

    ProcessedKafkaRequestExceptionType(Status status, int exceptionCode, String message) {
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
