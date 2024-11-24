package com.HungTran.MeetingTeam.WebSocket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.HungTran.MeetingTeam.Util.InfoChecking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.TeamRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Security.CustomUserDetails;
import com.HungTran.MeetingTeam.Service.UserService;
import com.HungTran.MeetingTeam.Util.Constraint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
	private final Logger LOGGER=LoggerFactory.getLogger(WebSocketEventListener.class);
	private final SimpMessageSendingOperations messageTemplate;
	@Autowired
	TeamRepo teamRepo;
	@Autowired
	UserRepo userRepo;
	@Autowired
	InfoChecking infoChecking;
	@EventListener
	public void handleWebSocketConnectListener(
			SessionConnectedEvent event) {
		 StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
		 Authentication auth=(Authentication) headerAccessor.getUser();
		 CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
		 User u=userDetails.getU();
		 var chatMessage=Message.builder()
						.messageType(Constraint.ONLINE)
						.senderId(u.getId())
						.build();
			List<String> teamIds=teamRepo.getTeamIdsByUserId(u.getId());
			for(String teamId: teamIds) {
				messageTemplate.convertAndSend("/queue/"+teamId+"/chat",chatMessage);
			}
			List<String> friendIds=userRepo.getFriendIds(u.getId());
			for(String friendId: friendIds) {
				messageTemplate.convertAndSendToUser(friendId,"/messages",chatMessage);
			}
			userRepo.updateStatusAndLastActive(Constraint.ONLINE,LocalDateTime.now(),u.getId());
			LOGGER.info("New websocket connection-UserId: "+u.getId());
	 }
	@EventListener
	public void handleWebSocketDisconnectListener(
			SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
		 Authentication auth=(Authentication) headerAccessor.getUser();
		 CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
		 User u=userDetails.getU();
		var chatMessage=Message.builder()
				.messageType(Constraint.OFFLINE)
				.senderId(u.getId())
				.createdAt(LocalDateTime.now())
				.build();
		List<String> teamIds=teamRepo.getTeamIdsByUserId(u.getId());
		for(String teamId: teamIds) {
			messageTemplate.convertAndSend("/queue/"+teamId+"/general",chatMessage);
		}
		List<String> friendIds=userRepo.getFriendIds(u.getId());
		for(String friendId: friendIds) {
			messageTemplate.convertAndSendToUser(friendId,"/messages",chatMessage);
		}
		userRepo.updateStatusAndLastActive(Constraint.OFFLINE,LocalDateTime.now(),u.getId());
		LOGGER.info("User "+u.getId()+" disconnected");
	}
}
