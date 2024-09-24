package cjkimhello97.toy.crashMyServer.chat.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.BaseException;
import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;

public class ChatException extends BaseException {

    public ChatException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
