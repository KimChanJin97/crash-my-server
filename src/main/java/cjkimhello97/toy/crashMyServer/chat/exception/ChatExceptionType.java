package cjkimhello97.toy.crashMyServer.chat.exception;

import static cjkimhello97.toy.crashMyServer.common.exception.support.Status.*;

import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;
import cjkimhello97.toy.crashMyServer.common.exception.support.Status;

public enum ChatExceptionType implements ExceptionType {

    CHAT_ROOM_NOT_FOUND(SERVER_ERROR, 3001, "채팅방이 존재하지 않습니다."),
    MEMBER_CHAT_ROOM_TABLE_NOT_EXIST(SERVER_ERROR, 3002, "채팅방을 퇴장했기 때문에 채팅 조회가 불가능합니다.")
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
