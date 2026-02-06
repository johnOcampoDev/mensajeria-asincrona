package com.company.messagegateway.exception;

public class MessagePublishingException extends RuntimeException {

	public MessagePublishingException(String message, Throwable cause) {
		super(message, cause);
	}
}