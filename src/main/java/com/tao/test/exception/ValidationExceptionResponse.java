package com.tao.test.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Value;

@Value
public class ValidationExceptionResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss a")
	LocalDateTime timestamp;
	Map<String, String> errors;
	String details;


	public ValidationExceptionResponse(String message, String details, Map<String, String> errors) {
		this.timestamp = LocalDateTime.now();
		this.errors = errors;
		this.details = details;
	}
}
