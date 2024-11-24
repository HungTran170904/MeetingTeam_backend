package com.HungTran.MeetingTeam.Controller;


import java.util.List;

import com.HungTran.MeetingTeam.DTO.CalendarDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HungTran.MeetingTeam.DTO.MeetingDTO;
import com.HungTran.MeetingTeam.Model.MessageReaction;
import com.HungTran.MeetingTeam.Service.MeetingService;

@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingController {
	private final MeetingService meetingService;

	@PostMapping("/createMeeting")
	public ResponseEntity<String> createMeeting(
			@RequestBody MeetingDTO dto){
		return ResponseEntity.ok(meetingService.createMeeting(dto));
	}
	@PutMapping("/updateMeeting")
	public ResponseEntity<HttpStatus> updateMeeting(
			@RequestBody MeetingDTO dto){
		meetingService.updateMeeting(dto);
		return new ResponseEntity(HttpStatus.OK);
	}

	@MessageMapping("/meetingReaction/{meetingId}")
	public void reactMeeting(
			@Payload MessageReaction reaction,
			@DestinationVariable("meetingId") String meetingId) {
		meetingService.reactMeeting(meetingId, reaction);
	}

	@GetMapping("/registerEmailNotification")
	public ResponseEntity<HttpStatus> registerEmailNotification(
			@RequestParam("meetingId") String meetingId,
			@RequestParam("receiveEmail") Boolean receiveEmail){
		meetingService.registerEmailNotification(meetingId, receiveEmail);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/addToCalendar")
	public ResponseEntity<HttpStatus> addToCalendar(
			@RequestParam("meetingId") String meetingId,
			@RequestParam("isAdded") Boolean isAdded){
		meetingService.addToCalendar(meetingId,isAdded);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/meetingsOfWeek/{week}")
	public ResponseEntity<CalendarDTO> getMeetingsOfWeek(
			@PathVariable("week") Integer week){
		return ResponseEntity.ok(meetingService.getMeetingsOfWeek(week));
	}

	@GetMapping("/getVideoChannelMeetings")
	public ResponseEntity<List<MeetingDTO>> getVideoChannelMeetings(
			@RequestParam("channelId") String channelId,
			@RequestParam("receivedMeetingNum") Integer receivedMeetingNum){
		return ResponseEntity.ok(meetingService.getVideoChannelMeetings(channelId,receivedMeetingNum));
	}

	@GetMapping("/generateToken")
	public ResponseEntity<ObjectNode> generateToken(
			@RequestParam("meetingId") String meetingId){
		return ResponseEntity.ok(meetingService.generateToken(meetingId));
	}

	@DeleteMapping("/cancelMeeting/{meetingId}")
	public ResponseEntity<HttpStatus> cancelMeeting(
			@PathVariable("meetingId") String meetingId){
		meetingService.cancelMeeting(meetingId);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("/deleteMeeting/{meetingId}")
	public ResponseEntity<HttpStatus> deleteMeeting(
			@PathVariable("meetingId") String meetingId){
		meetingService.deleteMeeting(meetingId);
		return new ResponseEntity(HttpStatus.OK);
	}
}
