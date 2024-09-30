package cjkimhello97.toy.crashMyServer.chat.exception;

import static cjkimhello97.toy.crashMyServer.common.exception.support.Status.*;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum ChatExceptionType implements ExceptionType {

    CHAT_ROOM_NOT_FOUND(SERVER_ERROR, 3001, "CHAT ROOM NOT EXIST"),
    MEMBER_CHAT_ROOM_TABLE_NOT_EXIST(SERVER_ERROR, 3002, "ALREADY LEFT CHAT ROOM")
    ;

    private final Status status;
    private final int exceptionCode;
    private final String message;

    ChatExceptionType(Status status, int exceptionCode, String message) {
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
