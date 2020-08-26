package it.polito.ai.es2;

import it.polito.ai.es2.services.exceptions.*;
import lombok.extern.java.Log;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Log
@ControllerAdvice
public class GlobalRuntimeExceptionHandler
    extends ResponseEntityExceptionHandler {
  @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
  protected ResponseEntity<Object> genericError(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = "Server Error";
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request); // 500
  }
  
  @ExceptionHandler(value = {DataAccessException.class, ConstraintViolationException.class, TransactionSystemException.class, RollbackException.class})
  protected ResponseEntity<Object> dataError(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = "Data Error";
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request); // 400
  }
  
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    log.warning("Validation errors: " + errors.values());
    return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY); // 422
  }
  
  @ExceptionHandler(value = {NullParameterException.class})
  protected ResponseEntity<Object> nullParameters(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = ex.getMessage();
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request); // 400
  }
  
  @ExceptionHandler(value = {VlException.class})
  protected ResponseEntity<Object> genericApiError(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = ex.getMessage();
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request); // 400
  }
  
  @ExceptionHandler(value = {CourseNotFoundException.class, StudentNotFoundException.class})
  protected ResponseEntity<Object> notFound(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = ex.getMessage();
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request); // 404
  }
  
  @ExceptionHandler(value = {CourseCardinalityConstrainsException.class})
  protected ResponseEntity<Object> courseTeamsCardinalityViolation(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = ex.getMessage();
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request); // 404
  }
  
  @ExceptionHandler(value = {CourseNotEnabledException.class})
  protected ResponseEntity<Object> CourseNotEnabled(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = ex.getMessage();
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request); // 404
  }
  
  @ExceptionHandler(value = {StudentAlreadyEnrolled.class})
  protected ResponseEntity<Object> StudentAlreadyEnrolled(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = ex.getMessage();
    log.warning(ex.toString() + " \n " + request.toString());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request); // 404
  }
}
