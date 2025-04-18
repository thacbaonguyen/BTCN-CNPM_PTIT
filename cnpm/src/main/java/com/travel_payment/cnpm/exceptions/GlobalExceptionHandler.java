package com.travel_payment.cnpm.exceptions;

import com.travel_payment.cnpm.dto.response.ApiResponse;
import com.travel_payment.cnpm.exceptions.common.AlreadyException;
import com.travel_payment.cnpm.exceptions.common.AppException;
import com.travel_payment.cnpm.exceptions.common.InvalidException;
import com.travel_payment.cnpm.exceptions.common.NotFoundException;
import com.travel_payment.cnpm.exceptions.user.EmailSenderException;
import com.travel_payment.cnpm.exceptions.user.PermissionException;
import com.travel_payment.cnpm.utils.TravelResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(NotFoundException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyException(AlreadyException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<ApiResponse> handleInvalidException(InvalidException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiResponse> handlePermissionException(PermissionException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailSenderException.class)
    public ResponseEntity<ApiResponse> handleEmailSenderException(EmailSenderException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //handle input
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return TravelResponse.generateResponse(errors, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLDataException.class)
    public ResponseEntity<ApiResponse> handleSQLDataException(SQLDataException ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        log.error("exception details: {}", ex.getMessage(), ex.getCause());
        return TravelResponse.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
