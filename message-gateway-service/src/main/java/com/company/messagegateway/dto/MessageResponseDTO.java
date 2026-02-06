package com.company.messagegateway.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class MessageResponseDTO {

	private String status;
	private String message;
	private Instant timestamp;
	
    public MessageResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

}
