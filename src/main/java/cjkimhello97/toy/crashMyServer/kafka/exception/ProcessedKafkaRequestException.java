package cjkimhello97.toy.crashMyServer.kafka.exception;

import cjkimhello97.toy.crashMyServer.common.exception.support.BaseException;
import cjkimhello97.toy.crashMyServer.common.exception.support.ExceptionType;

public class ProcessedKafkaRequestException extends BaseException {

    public ProcessedKafkaRequestException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
