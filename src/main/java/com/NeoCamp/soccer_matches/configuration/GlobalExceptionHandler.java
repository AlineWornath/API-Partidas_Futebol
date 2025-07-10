package com.neocamp.soccer_matches.configuration;

import com.neocamp.soccer_matches.dto.util.ErrorResponse;
import com.neocamp.soccer_matches.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFound(
            EntityNotFoundException e, HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String message = e.getMessage();

        log.error("Entity not found - [{} {}] Path: {} | Query: {} | Message: {}",
                method, path, path, query, message, e);

        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), null,
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException(
            BusinessException e, HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String message = e.getMessage();

        log.warn("BusinessException - [{}] Path: {} | Query: {} | Message: {}",
                method, path, query, message, e);

        ErrorResponse errorResponse = new ErrorResponse(message, null,
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errorMessages = new ArrayList<>();

        for(ObjectError error : e.getBindingResult().getAllErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();

        log.error("Validation error - [{} {}]  Path: {} | Params: {} | Errors: {}",
                method, path, path, query, errorMessages, e);

        ErrorResponse errorResponse = new ErrorResponse(null, errorMessages,
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> typeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String method =request.getMethod();
        String path = request.getRequestURI();
        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        String receivedValue = String.valueOf(e.getValue());

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected a value of type '%s'.",
                receivedValue, paramName, requiredType);

        log.warn("Parameter type mismatch - [{} {}]  Path: {} | Param: {} | Value: {} | Error: {}",
                method, path, path, paramName, receivedValue, e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(message, null, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgument(
            IllegalArgumentException e, HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String message = e.getMessage();

        log.warn("IllegalArgumentException - [{} {}]  Path: {} | Query: {} | Message: {}",
                method, path, path, query, message, e);

        ErrorResponse errorResponse = new ErrorResponse(message, null, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> otherErrors(Exception e, HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String message = e.getMessage();

        log.error("Unexpected error - [{} {}] Path: {} | Query: {} |Error: {}",
                method, path, path, query, message, e);

        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
