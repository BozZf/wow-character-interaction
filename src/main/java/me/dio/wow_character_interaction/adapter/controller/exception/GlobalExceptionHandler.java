package me.dio.wow_character_interaction.adapter.controller.exception;

import feign.FeignException;
import me.dio.wow_character_interaction.service.GeminiResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({FeignException.class, GeminiResponseException.class})
    public ResponseEntity<ResponseError> handleGeminiError() {
        return error(HttpStatus.BAD_GATEWAY, "Gemini could not provide an answer");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleIllegalArgument(IllegalArgumentException exception) {
        String message = exception.getMessage();
        return error(HttpStatus.BAD_REQUEST, message == null ? "Invalid request" : message);
    }

    @ExceptionHandler(RequiredObjectIsNull.class)
    public ResponseEntity<ResponseError> handleRequiredObject() {
        return error(HttpStatus.BAD_REQUEST, "A request body is required");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ResponseError> handleMalformedRequest() {
        return error(HttpStatus.BAD_REQUEST, "Invalid or missing request data");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseError> handleNotFound() {
        return error(HttpStatus.NOT_FOUND, "Resource not found");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseError> handleAuthentication() {
        return error(HttpStatus.UNAUTHORIZED, "Invalid credentials or token");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseError> handleAccessDenied() {
        return error(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleException() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Sorry, an error occurred");
    }

    private ResponseEntity<ResponseError> error(HttpStatus status, String message) {
        ResponseError body = new ResponseError(status.getReasonPhrase(), status.value(), message);
        body.setTimestamp(new Date());
        return ResponseEntity.status(status).body(body);
    }
}
