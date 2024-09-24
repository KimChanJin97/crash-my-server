package cjkimhello97.toy.crashMyServer.click.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.BaseException;
import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;

public class ClickException extends BaseException {

    public ClickException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
