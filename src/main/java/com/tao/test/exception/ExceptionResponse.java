package com.tao.test.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Value;

@Value
public class ExceptionResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss a")
	LocalDateTime timestamp;
	String message;
	String details;


	public ExceptionResponse(String message, String details) {
		this.timestamp = LocalDateTime.now();
		this.message = message;
		this.details = details;
	}
}