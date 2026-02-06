package com.company.messageprocessor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQInfraConfig {

	@Value("${rabbitmq.queue.name}")
	private String queueName;

	@Bean
	public DirectExchange mainExchange() {
		return new DirectExchange("message.exchange", true, false);
	}

	@Bean
	public DirectExchange dlxExchange() {
		return new DirectExchange("message.dlx", true, false);
	}

	@Bean
	public Queue messageQueue() {
		return QueueBuilder.durable(queueName).withArgument("x-dead-letter-exchange", "message.dlx")
				.withArgument("x-dead-letter-routing-key", "message.dlq").build();
	}

	@Bean
	public Queue deadLetterQueue() {
		return QueueBuilder.durable("message.dlq").build();
	}

	@Bean
	public Binding mainBinding() {
		return BindingBuilder.bind(messageQueue()).to(mainExchange()).with("message.routing.key");
	}

	@Bean
	public Binding dlqBinding() {
		return BindingBuilder.bind(deadLetterQueue()).to(dlxExchange()).with("message.dlq");
	}
}
