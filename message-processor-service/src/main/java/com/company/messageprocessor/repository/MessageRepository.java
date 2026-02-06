package com.company.messageprocessor.repository;

import java.time.Instant;
import java.util.List;

import com.company.messageprocessor.model.MessageDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<MessageDocument, String> {

	long countByDestinationAndCreatedDateAfter(String destination, Instant dateTime);

	List<MessageDocument> findByDestination(String destination);
}
