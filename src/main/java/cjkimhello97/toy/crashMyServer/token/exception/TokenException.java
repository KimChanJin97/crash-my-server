package cjkimhello97.toy.crashMyServer.redis.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.BaseException;
import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;

public class TokenException extends BaseException {

    public TokenException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
