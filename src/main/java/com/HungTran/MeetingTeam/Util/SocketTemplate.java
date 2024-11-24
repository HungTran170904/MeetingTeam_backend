package com.HungTran.MeetingTeam.Util;

import com.HungTran.MeetingTeam.DTO.SocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SocketTemplate {
    @Autowired
    SimpMessagingTemplate messageTemplate;

    public void sendUser(String userId, String topic, Object payload) {
        SocketMessage sockMsg = new SocketMessage(topic, payload);
        messageTemplate.convertAndSend("/topic/user."+userId, sockMsg);
    }
    public void sendTeam(String teamId, String topic, Object payload) {
        SocketMessage sockMsg = new SocketMessage(topic, payload);
        messageTemplate.convertAndSend("/topic/team."+teamId, sockMsg);
    }
}
