/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.JDBCException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.openapitools.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

// used examples from https://dzone.com/articles/leverage-http-status-codes-to-build-a-rest-service to setup this error handling
// https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
// https://niels.nu/blog/2016/controller-advice-exception-handlers.html
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_LOG_STR = "Error : {} ";
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler({ AlreadyExistsException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<ErrorResponse> handleAlreadyExistsExceptionHandler(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse().message(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ResponseBody
    @ExceptionHandler({ ResourceNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<ErrorResponse> handleResourceNotFoundException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse().message(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> processValidationError(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : constraintViolations) {
            String name = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            errors.add(name + " " + violation.getMessage());
        }
        ErrorResponse error = new ErrorResponse().message(String.join(",", errors));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> processBadOrderByProperty(PropertyReferenceException ex) {
        String errorMsg = "No property " + ex.getPropertyName() + " to orderBy for this endpoint";
        ErrorResponse error = new ErrorResponse().message(errorMsg);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({ CustomValidationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse> handleTemplateEmptyException(CustomValidationException ex) {
        ErrorResponse error = new ErrorResponse().message(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler({ HttpMessageConversionException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse> handleInvalidDefinitionException(HttpMessageConversionException ex, WebRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidDefinitionException) {
            InvalidDefinitionException invalidDeg = (InvalidDefinitionException) cause;
            ErrorResponse error = new ErrorResponse().message(invalidDeg.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return this.handleUncaughtException(ex, request);
    }

    // **********************************************************************
    // ********** Custom HttpStatus.INTERNAL_SERVER_ERROR handling **********
    // **********************************************************************
    @ResponseBody
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleUncaughtException(RuntimeException ex, WebRequest request) {
        LOG.error(ERROR_LOG_STR, ex);
        ErrorResponse error = new ErrorResponse().message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JDBCException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleJDBCException(JDBCException ex) {
        LOG.error(ERROR_LOG_STR, ex);
        ErrorResponse error = new ErrorResponse().message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ******************************************
    // ********** Overridden Functions **********
    // ******************************************
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse error = new ErrorResponse().message(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        ErrorResponse error = new ErrorResponse().message(builder.toString());
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse().message(String.join(",", errors));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOG.error(ERROR_LOG_STR, ex.getMessage());
        ErrorResponse error = new ErrorResponse().message(status.getReasonPhrase());
        return new ResponseEntity<>(error, status);
    }
}
