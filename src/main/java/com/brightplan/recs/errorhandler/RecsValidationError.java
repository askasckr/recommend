package com.brightplan.recs.errorhandler;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * A field errors thrown at Rest controller level validation.
 */
public class RecsValidationError {

  private List<RecsFieldError> fieldErrors = Lists.newArrayList();

  public RecsValidationError() {

  }

  public void addFieldError(String path, String message) {
    RecsFieldError error = new RecsFieldError(path, message);
    fieldErrors.add(error);
  }

  public List<RecsFieldError> getFieldErrors() {
    return fieldErrors;
  }
}
