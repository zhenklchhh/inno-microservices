package com.innowise.orderservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class UserNotFoundInUserServiceException extends RuntimeException {
    public UserNotFoundInUserServiceException(String message) {
        super(message);
    }

  public UserNotFoundInUserServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
