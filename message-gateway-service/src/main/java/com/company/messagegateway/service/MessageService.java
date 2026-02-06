package com.company.messagegateway.service;

import com.company.messagegateway.dto.MessageRequestDTO;

public interface MessageService {
	void processAndSend(MessageRequestDTO request);
}