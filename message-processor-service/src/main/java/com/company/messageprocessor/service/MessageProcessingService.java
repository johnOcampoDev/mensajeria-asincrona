package com.company.messageprocessor.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.company.messageprocessor.dto.MessageRequestDTO;
import com.company.messageprocessor.model.MessageDocument;
import com.company.messageprocessor.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageProcessingService {

	private final MessageRepository repository;
	private final ObjectMapper objectMapper;

	public MessageProcessingService(MessageRepository repository, ObjectMapper objectMapper) {
		this.repository = repository;
		this.objectMapper = objectMapper;
	}

	public void process(Message message, long receivedTimestamp) {

		MessageDocument doc = new MessageDocument();
		doc.setCreatedDate(Instant.now());

		try {
			MessageRequestDTO payload = objectMapper.readValue(message.getBody(), MessageRequestDTO.class);

			doc.setOrigin(payload.getOrigin());
			doc.setDestination(payload.getDestination());
			doc.setMessageType(payload.getMessageType());
			doc.setContent(payload.getContent());

			Instant last24h = Instant.now().minus(24, ChronoUnit.HOURS);

			long count = repository.countByDestinationAndCreatedDateAfter(payload.getDestination(), last24h);

			if (count >= 3) {
				doc.setError("DESTINATION_MESSAGE_LIMIT_EXCEEDED");
			}

		} catch (Exception e) {
			doc.setError("PROCESSING_ERROR");
		}

		long processingTime = System.currentTimeMillis() - receivedTimestamp;
		doc.setProcessingTime(processingTime);

		repository.save(doc);
	}
}
