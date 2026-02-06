package com.company.messageprocessor.listener;

import java.io.IOException;

import com.company.messageprocessor.service.MessageProcessingService;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private final MessageProcessingService service;

    public MessageListener(MessageProcessingService service) {
        this.service = service;
    }

    @RabbitListener(
        queues = "${rabbitmq.queue.name}",
        ackMode = "MANUAL"
    )
    public void consume(
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag,
            @Header("receivedTimestamp") long receivedTimestamp) throws IOException {

        try {
            service.process(message, receivedTimestamp);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, false); // va a DLQ
        }
    }
}

