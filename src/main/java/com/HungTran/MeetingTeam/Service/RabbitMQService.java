package com.HungTran.MeetingTeam.Service;

import com.HungTran.MeetingTeam.Converter.ReactionConverter;
import com.HungTran.MeetingTeam.Model.Meeting;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RabbitMQService {
	private final MailService mailService;
	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.add-task-queue}")
	private String addedTaskKey;
	@Value("${rabbitmq.remove-task-queue}")
	private String removedTaskKey;
	@Value("${rabbitmq.exchange-name}")
	private String exchange;
	private Logger LOGGER= LoggerFactory.getLogger(ReactionConverter.class);

	@RabbitListener(queues="${rabbitmq.notification-queue}")
	public void listenRabbitMQMessage(ObjectNode jsonObject) {
		try{
			LOGGER.info("RabbitMQ Message Received: {meetingId:"+jsonObject.get("meetingId")+"-time:"+jsonObject.get("time"));
			String meetingId= jsonObject.get("meetingId").asText();
			LocalDateTime time=LocalDateTime.parse(jsonObject.get("time").asText());
			mailService.sendEmailNotification(meetingId,time);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void sendAddedTaskMessage(Meeting meeting){
		LOGGER.info("Send addedTask message: "+meeting.toString());
		rabbitTemplate.convertAndSend(exchange, addedTaskKey, meeting);
	}
	public void sendRemovedTaskMessage(String meetingId){
		LOGGER.info("Send removedTask message: "+meetingId);
		rabbitTemplate.convertAndSend(exchange, removedTaskKey, meetingId);
	}
}
