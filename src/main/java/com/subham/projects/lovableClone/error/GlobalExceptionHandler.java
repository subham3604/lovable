package com.subham.projects.lovableClone.error;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getResourceName() + " with id " + ex.getResourceId() + " not found.");
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInputValidationError(MethodArgumentNotValidException ex) {
        List<ApiFieldError> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> new ApiFieldError(error.getField(), error.getDefaultMessage())).toList();

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Input Validation Failed", errors);
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenRequests(ForbiddenException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknown(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(DataIntegrityViolationException ex) {

        ApiError apiError = new ApiError(HttpStatus.CONFLICT, "Resource already exists or duplicate entry.");

        log.error("Database constraint violation", ex);

        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDenied(Exception ex) {

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Access Denied");

        log.error("Authorization failed", ex);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Username not found with username: " + ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Access denied: Insufficient permissions");
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }
}
