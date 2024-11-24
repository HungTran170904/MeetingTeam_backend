package com.HungTran.MeetingTeam.Controller;


import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.MessageReaction;
import com.HungTran.MeetingTeam.Service.ChatService;
import com.HungTran.MeetingTeam.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

	@MessageMapping("/message")
	public void receiveMessage(@Payload Message message) {
		chatService.receivePublicChatMessage(message, null);
	}
	@MessageMapping("/privateMessage") 
	public void receivePrivateMessage(
			@Payload Message message) {
		chatService.receivePrivateChatMessage(message, null);
	}
	@MessageMapping("/messageReaction/{messageId}")
	public void reactMessage(
			@Payload MessageReaction reaction,
			@DestinationVariable("messageId") Integer messageId) {
		chatService.reactMessage(messageId, reaction);
	}
	@PostMapping("/fileMessage")
	public ResponseEntity<HttpStatus> receiveFileMessage(
			@RequestParam("message") String messageJson,
			@RequestParam(value="file") MultipartFile file) throws Exception{
		Message message=objectMapper.readValue(messageJson,Message.class);
		chatService.receivePublicChatMessage(message, file);
		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/privateFileMessage")
	public ResponseEntity<HttpStatus> receivePrivateFileMessage(
			@RequestParam("message") String messageJson,
			@RequestParam(value="file") MultipartFile file) throws Exception{
		Message message=objectMapper.readValue(messageJson,Message.class);
		chatService.receivePrivateChatMessage(message, file);
		return new ResponseEntity(HttpStatus.OK);
	}
	@GetMapping("/getPrivateMessages")
	public ResponseEntity<List<Message>> getPrivateMessages(
			@RequestParam("receivedMessageNum") Integer receivedMessageNum,
			@RequestParam("friendId") String friendId){
		return ResponseEntity.ok(chatService.getPrivateMessages(receivedMessageNum,friendId));
	}
	@GetMapping("/getTextChannelMessages")
	public ResponseEntity<List<Message>> getTextChannelMessages(
			@RequestParam("receivedMessageNum") Integer receivedMessageNum,
			@RequestParam("channelId") String channelId){
		return ResponseEntity.ok(chatService.getTextChannelMessages(receivedMessageNum,channelId));
	}
	@MessageMapping("/unsendMessage/{id}")
	public void unsendMessage(
			@DestinationVariable("id") Integer messageId){
		chatService.unsendMessage(messageId);
	}
}
