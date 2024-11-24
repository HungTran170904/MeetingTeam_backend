package com.HungTran.MeetingTeam.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	@Value("${rabbitmq.notification-queue}")
	private String notificationQueue;
	@Value("${rabbitmq.add-task-queue}")
	private String addedTaskQueue;
	@Value("${rabbitmq.remove-task-queue}")
	private String removedTaskQueue;
	@Value("${rabbitmq.exchange-name}")
	private String exchange;

	@Bean
	public Queue notificationQueue() {
		return new Queue(notificationQueue);
	}
	@Bean
	public Queue addTaskQueue() {
		return new Queue(addedTaskQueue);
	}
	@Bean
	public Queue removeTaskQueue() {
		return new Queue(removedTaskQueue);
	}
	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(exchange);
	}
	@Bean
	public Binding notificationBinding() {
		return BindingBuilder.bind(notificationQueue())
				.to(exchange())
				.with(notificationQueue);
	}
	@Bean
	public Binding addedTaskBinding() {
		return BindingBuilder.bind(addTaskQueue())
				.to(exchange())
				.with(addedTaskQueue);
	}
	@Bean
	public Binding removedTaskBinding() {
		return BindingBuilder.bind(removeTaskQueue())
				.to(exchange())
				.with(removedTaskQueue);
	}
	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}
}
