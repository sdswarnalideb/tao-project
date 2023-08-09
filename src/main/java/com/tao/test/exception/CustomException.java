package com.tao.test.exception;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 3854058871175688734L;

	public CustomException(String errorMessage) {
		super(errorMessage);
	}
}
