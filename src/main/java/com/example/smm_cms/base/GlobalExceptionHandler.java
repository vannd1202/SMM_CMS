package com.example.smm_cms.base;

import com.example.smm_cms.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // ===== VALIDATION ERROR =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<ErrorResponse.FieldErrorDetail> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(err -> new ErrorResponse.FieldErrorDetail(
                                err.getField(),
                                err.getDefaultMessage()
                        ))
                        .toList();

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // ===== RUNTIME ERROR =====
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseData<?>> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData().error(500, ex.getMessage()));
    }

    // ===== GENERAL ERROR =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<?>> handleException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseData().error(500, "Internal Server Error"));
    }
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseData<?>> handleBusiness(BaseException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseData().error(ex.getCode(), ex.getMessage()));
    }

}
