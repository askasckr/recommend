package com.brightplan.recs.errorhandler;

/**
 * A field error thrown at Rest controller level validation.
 */
public class RecsFieldError {

  private String field;

  private String message;

  public RecsFieldError(String field, String message) {
    this.field = field;
    this.message = message;
  }

  public String getField() {
    return field;
  }

  public String getMessage() {
    return message;
  }

}
