package com.company.messageprocessor.controller;

import java.util.List;

import com.company.messageprocessor.model.MessageDocument;
import com.company.messageprocessor.repository.MessageRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageQueryController {

	private final MessageRepository repository;

	public MessageQueryController(MessageRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/destination/{destination}")
	public List<MessageDocument> getByDestination(@PathVariable String destination) {

		return repository.findByDestination(destination);
	}
}
