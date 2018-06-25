package com.brightplan.recs.errorhandler;

import com.brightplan.recs.filter.RecsHttpRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(3)
class RecsUnhandledExceptionHandler {

  private static Logger Log = LoggerFactory.getLogger(RecsUnhandledExceptionHandler.class);

  @ExceptionHandler(value = Exception.class)
  public void defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
    Log.error(
        req.getMethod() + " " + req.getRequestURI() + " " + req.getQueryString() + " " + System
            .lineSeparator() + new String(((RecsHttpRequestWrapper) req).getRequestBody()) + System
            .lineSeparator() + ExceptionUtils.getStackTrace(e));
    // Send this as pager duty alert
    // rabbitService.logError(e);
    throw e;
  }
}