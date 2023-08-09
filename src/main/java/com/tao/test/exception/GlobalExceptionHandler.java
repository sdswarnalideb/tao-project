package com.tao.test.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final String environment;

	@Autowired
	public GlobalExceptionHandler(@Value("${spring.profiles.active}") String environment) {
		this.environment = environment;
	}

	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public final ResponseEntity<ExceptionResponse> handleCustomExceptions(Exception ex,
			WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getLocalizedMessage(),
				request.getDescription(false));
		log.error(exceptionResponse.getMessage(), ex, req);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public final ResponseEntity<ExceptionResponse> handleResponseStatusExceptions(
			ResponseStatusException ex, WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getLocalizedMessage(),
				request.getDescription(false));
		if (ex.getStatus().isError()) { //Log only if error
			log.error(ex.getMessage(), ex, req);
		}
		return new ResponseEntity<>(exceptionResponse, ex.getStatus());
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public final ResponseEntity<ExceptionResponse> handleForbiddenExceptions(Exception ex,
			WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getLocalizedMessage(),
				request.getDescription(false));

		log.error(exceptionResponse.getMessage(), ex, req);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(Exception ex,
			WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getLocalizedMessage(),
				request.getDescription(false));

		log.error(exceptionResponse.getMessage(), ex, req);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ExceptionResponse error = new ExceptionResponse("Validation Failed. " + details,
				request.getDescription(false));

		log.error(error.getMessage(), ex);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(ConversionFailedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public final ResponseEntity<ExceptionResponse> handleIllegalExceptions(Exception ex,
			WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),
				request.getDescription(false));

		log.error(exceptionResponse.getMessage(), ex, req);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public final ResponseEntity<ExceptionResponse> handleConstraintExceptions(Exception ex,
			WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(),
				request.getDescription(false));

		log.error(exceptionResponse.getMessage(), ex, req);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex,
			WebRequest request, HttpServletRequest req) {
		ExceptionResponse exceptionResponse = new ExceptionResponse(
				this.environment.equals("prod") ? "Something went wrong" : ex.getMessage(),
				request.getDescription(false));
		if (!this.environment.equals("prod")) {
			ex.printStackTrace();
		}
		log.error(ex.getMessage(), ex, req);
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ValidationExceptionResponse errorDetails = new ValidationExceptionResponse(
				this.environment.equals("prod") ? "Something went wrong" : ex.getMessage(),
				request.getDescription(false), errors);
		return handleExceptionInternal(ex, errorDetails, headers, HttpStatus.BAD_REQUEST,
				request);
	}

}
