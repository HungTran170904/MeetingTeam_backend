package com.HungTran.MeetingTeam.Controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HungTran.MeetingTeam.DTO.RequestMessageDTO;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Service.FriendRequestService;

@RestController
@RequestMapping("/api/friendRequest")
@RequiredArgsConstructor
public class FriendRequestController {
	private final FriendRequestService frService;

	@GetMapping("/acceptFriend")
	public ResponseEntity<HttpStatus> acceptFriend(
			@RequestParam("messageId") Integer messageId){
		frService.acceptFriend(messageId);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/sendFriendRequest")
	public ResponseEntity<HttpStatus> sendFriendRequest(
			@RequestParam("email") String email,
			@RequestParam("content") String content){
		frService.friendRequest(email, content);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/getFriendRequests")
	public ResponseEntity<List<RequestMessageDTO>> getFriendRequests(){
		return ResponseEntity.ok(frService.getFriendRequests());
	}

	@DeleteMapping("/deleteFriendRequest/{id}")
	public ResponseEntity<HttpStatus> deleteFriendRequest(
			@PathVariable("id") Integer id){
		frService.deleteFriendRequest(id);
		return new ResponseEntity(HttpStatus.OK);
	}
}
