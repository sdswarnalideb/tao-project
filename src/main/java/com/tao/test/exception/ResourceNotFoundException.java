package com.tao.test.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2947210857950100650L;

	public ResourceNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
