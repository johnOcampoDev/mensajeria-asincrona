package com.company.messagegateway.exception;

public class OriginNotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OriginNotAuthorizedException(String message) {
		super(message);
	}
}
