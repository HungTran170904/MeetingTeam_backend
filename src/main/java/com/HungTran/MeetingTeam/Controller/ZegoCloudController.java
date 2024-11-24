package com.HungTran.MeetingTeam.Controller;

import com.HungTran.MeetingTeam.Service.ZegoCloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zegocloud")
@RequiredArgsConstructor
public class ZegoCloudController {
	private final ZegoCloudService zegoCloudService;

	@PostMapping("/room_created")
	public ResponseEntity<HttpStatus> roomCreatedNotification(
			@RequestParam("room_id") String roomId){
		zegoCloudService.createRoomNotification(roomId);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/room_close")
	public ResponseEntity<HttpStatus> roomCloseNotification(
			@RequestParam("room_id") String roomId){
		zegoCloudService.closeRoomNotification(roomId);
		return new ResponseEntity(HttpStatus.OK);
	}
}
