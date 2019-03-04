package com.quantchi.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends Exception {
  private static final long serialVersionUID = 5825023400372858863L;
}
