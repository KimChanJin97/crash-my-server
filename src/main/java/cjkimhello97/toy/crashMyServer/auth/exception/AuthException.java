package cjkimhello97.toy.crashMyServer.auth.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.BaseException;
import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;

public class AuthException extends BaseException {

    public AuthException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
