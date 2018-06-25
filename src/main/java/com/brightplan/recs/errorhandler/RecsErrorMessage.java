package com.brightplan.recs.errorhandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecsErrorMessage {

  private List<String> errors;

  public RecsErrorMessage() {
  }

  public RecsErrorMessage(List<String> errors) {
    this.errors = errors;
  }

  public RecsErrorMessage(String error) {
    this(Collections.singletonList(error));
  }

  public RecsErrorMessage(String... errors) {
    this(Arrays.asList(errors));
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }
}
