package cjkimhello97.toy.crashMyServer.common.exception.support;

import java.util.Arrays;
import org.springframework.http.HttpStatus;

public enum ExceptionStatus {

    SERVER_ERROR(Status.SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR), // 500
    NOT_FOUND(Status.FORBIDDEN, HttpStatus.NOT_FOUND), // 404
    UNAUTHORIZED(Status.UNAUTHORIZED, HttpStatus.UNAUTHORIZED), // 401
    FORBIDDEN(Status.FORBIDDEN, HttpStatus.FORBIDDEN), // 403
    BAD_REQUEST(Status.BAD_REQUEST, HttpStatus.BAD_REQUEST); // 400

    private final Status status;
    private final HttpStatus httpStatus;

    ExceptionStatus(Status status, HttpStatus httpStatus) {
        this.status = status;
        this.httpStatus = httpStatus;
    }

    public static ExceptionStatus from(Status input) {
        return Arrays.stream(ExceptionStatus.values())
                .filter(it -> it.status == input)
                .findAny()
                .orElse(SERVER_ERROR);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
