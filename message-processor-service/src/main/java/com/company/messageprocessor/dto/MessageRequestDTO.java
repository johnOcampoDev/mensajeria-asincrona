package com.company.messageprocessor.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequestDTO {
	@NotBlank
	private String origin;
	@NotBlank
	private String destination;
	@NotBlank
	private String messageType;
	@NotBlank
	private String content;
}
