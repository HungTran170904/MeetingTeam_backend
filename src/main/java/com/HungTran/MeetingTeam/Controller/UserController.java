package com.HungTran.MeetingTeam.Controller;


import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;
	private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

	@GetMapping("/getUserInfo")
	public ResponseEntity<UserDTO> getUserInfo(){
		return ResponseEntity.ok(userService.getUserInfo());
	}

	@PutMapping("/updateUser")
	public ResponseEntity<UserDTO> updateUser(
			@RequestParam("userDTO") String userJson,
			@RequestParam("currentPassword") String currentPassword,
			@RequestParam(name="file",required=false) MultipartFile file) throws Exception{
		System.out.println("UserJson:"+userJson);
		UserDTO dto=objectMapper.readValue(userJson,UserDTO.class);
		return ResponseEntity.ok(userService.updateUser(dto,currentPassword,file));
	}

	@GetMapping("/getFriends")
	public ResponseEntity<List<UserDTO>> getFriends(){
		return ResponseEntity.ok(userService.getFriends());
	}

	@DeleteMapping("/unfriend/{friendId}")
	public ResponseEntity<HttpStatus> unfriend(
			@PathVariable("friendId") String friendId){
		userService.unfriend(friendId);
		return new ResponseEntity(HttpStatus.OK);
	}
}
