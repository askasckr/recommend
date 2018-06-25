package com.brightplan.recs.errorhandler;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(1)
public class RecsExceptionHandler {

  @ExceptionHandler(value = RecsException.class)
  public ResponseEntity<String> handleEnsembleException(HttpServletRequest request,
      RecsException exception) {
    return new ResponseEntity<>(exception.getJSONMessage(), exception.getHttpCode());
  }
}
