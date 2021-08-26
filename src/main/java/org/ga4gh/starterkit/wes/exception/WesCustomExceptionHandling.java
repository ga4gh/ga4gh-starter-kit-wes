package org.ga4gh.starterkit.wes.exception;

import java.time.LocalDateTime;

import org.ga4gh.starterkit.common.constant.DateTimeConstants;
import org.ga4gh.starterkit.common.exception.CustomException;
import org.ga4gh.starterkit.common.util.webserver.CustomExceptionHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class WesCustomExceptionHandling extends CustomExceptionHandling {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomExceptions(CustomException err) {
        HttpStatus httpStatus = getCustomHttpStatus(err);
        return yieldResponseEntity(httpStatus, err);
    }

    private HttpStatus getCustomHttpStatus(CustomException err) {
        return err.getClass().getAnnotation(ResponseStatus.class).value();
    }

    private ResponseEntity<Object> yieldResponseEntity(HttpStatus httpStatus, Exception ex) {
        WesCustomExceptionResponse response = new WesCustomExceptionResponse();
        response.setTimestamp(LocalDateTime.now().format(DateTimeConstants.DATE_FORMATTER));
        response.setStatusCode(httpStatus.value());
        response.setError(httpStatus.getReasonPhrase());
        response.setMsg(ex.getMessage());
        return new ResponseEntity<>(response, httpStatus);
    }
}
