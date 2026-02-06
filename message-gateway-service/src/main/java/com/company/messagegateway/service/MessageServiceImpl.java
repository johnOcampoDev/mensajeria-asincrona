package com.company.messagegateway.service;

import com.company.messagegateway.dto.MessageRequestDTO;
import com.company.messagegateway.exception.OriginNotAuthorizedException;
import com.company.messagegateway.messaging.MessagePublisher;
import com.company.messagegateway.repository.AuthorizedOriginRepository;

import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

	private final AuthorizedOriginRepository originRepository;
	private final MessagePublisher messagePublisher;

	public MessageServiceImpl(AuthorizedOriginRepository originRepository, MessagePublisher messagePublisher) {
		this.originRepository = originRepository;
		this.messagePublisher = messagePublisher;
	}

	@Override
	public void processAndSend(MessageRequestDTO request) {

		if (!originRepository.existsByOriginCodeAndEnabledTrue(request.getOrigin())) {
			throw new OriginNotAuthorizedException("Origin not authorized: "+request.getOrigin());
		}

		messagePublisher.publish(request);
	}
}
