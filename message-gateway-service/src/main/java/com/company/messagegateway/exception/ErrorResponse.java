package com.company.messagegateway.exception;

import java.time.Instant;

import lombok.Data;

@Data
public class ErrorResponse {

	private String code;
	private String message;
	private Instant timestamp = Instant.now();

	public ErrorResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

}
