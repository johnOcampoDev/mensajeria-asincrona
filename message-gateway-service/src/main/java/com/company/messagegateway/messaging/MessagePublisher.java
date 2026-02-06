package com.company.messagegateway.messaging;

import com.company.messagegateway.dto.MessageRequestDTO;
import com.company.messagegateway.exception.MessagePublishingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {

	@Value("${spring.rabbitmq.template.exchange}")
	private String exchange;

	@Value("${spring.rabbitmq.template.routing-key}")
	private String routingKey;

	private final RabbitTemplate rabbitTemplate;

	private final ObjectMapper objectMapper;

	public MessagePublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
		this.rabbitTemplate = rabbitTemplate;
		this.objectMapper = objectMapper;
	}

	public void publish(MessageRequestDTO request) {

		try {

			Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(request))
					.setHeader("receivedTimestamp", System.currentTimeMillis()).build();

			rabbitTemplate.send(exchange, routingKey, message);

		} catch (JsonProcessingException e) {
			throw new MessagePublishingException("Error serializing message", e);
		}
	}

}
