package me.dio.wow_character_interaction.adapter.controller.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ResponseError> handleFeignClientException(FeignException.FeignClientException fce,
                                                                    WebRequest request) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "Gemini API communication error."
        );
        error.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException iae,
                                                                        WebRequest request) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "This character already exist."
        );
        error.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseError> handleNoSuchElementException(NoSuchElementException nsee, WebRequest request) {
        ResponseError error = new ResponseError(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "This ID does not exist."
        );
        error.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleException(Exception e, WebRequest request) {
        ResponseError error = new ResponseError(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred");
        error.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
