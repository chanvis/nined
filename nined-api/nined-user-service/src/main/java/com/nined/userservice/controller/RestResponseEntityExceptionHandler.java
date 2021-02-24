package com.nined.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nined.userservice.constants.UserConstants;
import com.nined.userservice.dto.GenericResponse;
import com.nined.userservice.exception.InvalidOldPasswordException;
import com.nined.userservice.exception.UserAlreadyExistException;
import com.nined.userservice.exception.UserDeleteNotAllowedException;
import com.nined.userservice.exception.UserNotFoundException;
import com.nined.userservice.exception.UserRoleNotAllowedException;

/**
 * Rest API response exception handler
 * 
 * @author vijay
 *
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID = "Invalid";
    private static final String ERROR_MSG_INVALID_MISSING = "Missing or invalid input params";

    @Autowired
    private MessageSource messages;

    public RestResponseEntityExceptionHandler() {
        super();
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse =
                new GenericResponse(result.getAllErrors(), INVALID + result.getObjectName(),
                        ERROR_MSG_INVALID_MISSING, HttpStatus.BAD_REQUEST.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        final BindingResult result = ex.getBindingResult();
        final GenericResponse bodyOfResponse =
                new GenericResponse(result.getAllErrors(), INVALID + result.getObjectName(),
                        ERROR_MSG_INVALID_MISSING, HttpStatus.BAD_REQUEST.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    // user
    @ExceptionHandler(value = {UserAlreadyExistException.class})
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex,
            final WebRequest request) {
        final GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage(),
                UserConstants.USER_ALREADY_EXISTS, HttpStatus.CONFLICT.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT,
                request);
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex,
            final WebRequest request) {
        final GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage(),
                UserConstants.USER_NOT_FOUND, HttpStatus.NOT_FOUND.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(value = {UserDeleteNotAllowedException.class})
    public ResponseEntity<Object> handleUserDeleteNotAllowed(final RuntimeException ex,
            final WebRequest request) {
        final GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage(),
                UserConstants.USER_DELETE_NOT_ALLOWED, HttpStatus.FORBIDDEN.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(value = {UserRoleNotAllowedException.class})
    public ResponseEntity<Object> handleUserRoleNotAllowed(final RuntimeException ex,
            final WebRequest request) {
        final GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage(),
                UserConstants.USER_ROLE_NOT_ALLOWED, HttpStatus.FORBIDDEN.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(value = {InvalidOldPasswordException.class})
    public ResponseEntity<Object> handleInvalidOldPassword(final RuntimeException ex,
            final WebRequest request) {
        final GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage(),
                UserConstants.INVALID_OLD_PASSWORD, HttpStatus.BAD_REQUEST.value());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }
}
