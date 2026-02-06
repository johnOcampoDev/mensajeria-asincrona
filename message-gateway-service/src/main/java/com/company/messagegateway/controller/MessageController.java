package com.company.messagegateway.controller;

import com.company.messagegateway.dto.MessageRequestDTO;
import com.company.messagegateway.dto.MessageResponseDTO;
import com.company.messagegateway.service.MessageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

	private final MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping
	public ResponseEntity<MessageResponseDTO> sendMessage(@Valid @RequestBody MessageRequestDTO request) {

		messageService.processAndSend(request);

		return ResponseEntity.accepted()
				.body(new MessageResponseDTO("ACCEPTED", "Message received and queued successfully"));
	}
}
