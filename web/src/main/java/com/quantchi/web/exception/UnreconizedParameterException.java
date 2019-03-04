package com.quantchi.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class UnreconizedParameterException extends RuntimeException {

  private static final long serialVersionUID = 6435926228396171188L;

  public UnreconizedParameterException(String message) {
    super(message);
  }

}
