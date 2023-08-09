package com.tao.test.exception;

public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 4292001101880737691L;

	public ForbiddenException(String message) {
		super(message);
	}

}
