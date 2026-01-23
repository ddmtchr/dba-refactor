package com.ddmtchr.dbarefactor.controller;

import com.ddmtchr.dbarefactor.exception.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(@NonNull RuntimeException ex, @NonNull WebRequest request) {
        logger.error(ex.getMessage(), ex);
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(@NonNull BadCredentialsException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.UNAUTHORIZED;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(NoAuthenticationException.class)
    public ResponseEntity<Object> handleNoAuthenticationException(@NonNull NoAuthenticationException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.UNAUTHORIZED;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleAlreadyExistsException(@NonNull UsernameAlreadyExistsException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(@NonNull IllegalStateException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(InconsistentRequestException.class)
    public ResponseEntity<Object> handleInconsistentRequestException(@NonNull InconsistentRequestException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(@NonNull InsufficientFundsException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(@NonNull NotFoundException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.NOT_FOUND;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<Object> handleNoPermissionException(@NonNull NoPermissionException ex, @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        HttpStatusCode status = HttpStatus.FORBIDDEN;
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null,  request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }
}
