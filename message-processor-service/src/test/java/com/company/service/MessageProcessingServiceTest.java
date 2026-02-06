package com.company.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.company.messageprocessor.model.MessageDocument;
import com.company.messageprocessor.repository.MessageRepository;
import com.company.messageprocessor.service.MessageProcessingService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageProcessingServiceTest {

    @Autowired
    private MessageRepository repository;

    @Autowired
    private MessageProcessingService service;

    @Test
    void shouldRejectWhenMoreThanThreeMessagesIn24Hours() {

        Instant now = Instant.now();

        for (int i = 0; i < 3; i++) {
            MessageDocument doc = new MessageDocument();
            doc.setDestination("573109876543");
            doc.setCreatedDate(now.minus(1, ChronoUnit.HOURS));
            repository.save(doc);
        }

        // aquí simulas el cuarto mensaje
        // y validas que error = DESTINATION_MESSAGE_LIMIT_EXCEEDED
    }
}
