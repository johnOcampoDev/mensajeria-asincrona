package com.company.messagegateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OriginNotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OriginNotAuthorizedException(String message) {
		super(message);
	}
}
