package com.db.awmd.challenge.exception;

public class NegativeAccountBalanceException extends RuntimeException {

  /**
	 * 
	 */
	private static final long serialVersionUID = 5795503430422713752L;

public NegativeAccountBalanceException(String message) {
    super(message);
  }
}
