package com.brightplan.recs.errorhandler;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
@Order(2)
public class RecsRestErrorHandler {

  private static Logger LOG = LoggerFactory.getLogger(RecsRestErrorHandler.class);

  @Autowired
  private MessageSource messageSource;

//  @Autowired
//  private Gson gson;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public RecsValidationError processValidationError(MethodArgumentNotValidException ex) {
    LOG.debug("Handling form validation error");

    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    RecsValidationError validationErrorDTO = processFieldErrors(fieldErrors);
    //rabbitService.logError(gson.toJson(validationErrorDTO));

    return validationErrorDTO;
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  @ResponseBody
  RecsErrorMessage getErrorMessage(HttpMediaTypeNotSupportedException ex) {
    String unsupported = "Unsupported content type: " + ex.getContentType();
    String supported =
        "Supported content types: " + MediaType.toString(ex.getSupportedMediaTypes());
    return new RecsErrorMessage(unsupported, supported);
  }

  @ExceptionHandler({TypeMismatchException.class,
      HttpMessageNotReadableException.class,
      ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  RecsErrorMessage handleExceptions(NestedRuntimeException ex) {
    return getMessage(ex.getMostSpecificCause(), ex.getMessage());
  }

  /**
   * Respond Bad Request on expected Exceptions without sending alerts. NOT SEND RABBIT
   * NOTIFICATION.
   */
  @ExceptionHandler({ClientAbortException.class,
      MultipartException.class,
      HttpRequestMethodNotSupportedException.class,
      ObjectOptimisticLockingFailureException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleExceptionsWithoutAlert() {
  }

  private RecsErrorMessage getMessage(Throwable mostSpecificCause, String message) {
    RecsErrorMessage errorMessage = new RecsErrorMessage();
    if (mostSpecificCause != null) {
      String exceptionName = mostSpecificCause.getClass().getName();
      String mostSpecificCauseMessage = mostSpecificCause.getMessage();
      errorMessage.setErrors(Arrays.asList(exceptionName, mostSpecificCauseMessage));
    } else {
      errorMessage.setErrors(Arrays.asList(message));
    }
    // rabbitService.logError(gson.toJson(errorMessage));
    return errorMessage;
  }

  private RecsValidationError processFieldErrors(List<FieldError> fieldErrors) {
    RecsValidationError dto = new RecsValidationError();

    for (FieldError fieldError : fieldErrors) {
      String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
      LOG.debug("Adding error message: {} to field: {}", localizedErrorMessage,
          fieldError.getField());
      dto.addFieldError(fieldError.getField(), localizedErrorMessage);
    }

    return dto;
  }

  private String resolveLocalizedErrorMessage(FieldError fieldError) {
    Locale currentLocale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(fieldError, currentLocale);
  }
}
