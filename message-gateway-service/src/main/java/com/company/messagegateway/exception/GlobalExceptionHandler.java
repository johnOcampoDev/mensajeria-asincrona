package com.company.messagegateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OriginNotAuthorizedException.class)
	public ResponseEntity<ErrorResponse> handleOriginException(OriginNotAuthorizedException ex) {

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErrorResponse("ORIGIN_NOT_AUTHORIZED", ex.getMessage()));
	}
}
