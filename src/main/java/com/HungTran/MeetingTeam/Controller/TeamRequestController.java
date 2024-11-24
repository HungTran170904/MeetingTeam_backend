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
import com.HungTran.MeetingTeam.Service.TeamRequestService;

@RestController
@RequestMapping("/api/teamRequest")
@RequiredArgsConstructor
public class TeamRequestController {
	private final TeamRequestService trService;

	@PostMapping("/requestToJoinTeam")
	public ResponseEntity<String> requestToJoinATeam(
			@RequestBody RequestMessage message){
		return ResponseEntity.ok(trService.requestToJoinTeam(message));
	}

	@GetMapping("/acceptNewMember")
	public ResponseEntity<HttpStatus> acceptNewMember(
			@RequestParam("teamId") String teamId,
			@RequestParam("messageId") Integer messageId){
		trService.acceptNewMember(teamId,messageId);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/getTeamRequestMessages")
	public ResponseEntity<List<RequestMessageDTO>> getTeamRequestMessages(
			@RequestParam("teamId") String teamId){
		return ResponseEntity.ok(trService.getTeamRequestMessages(teamId));
	}

	@GetMapping("/getSendedRequestMessages")
	public ResponseEntity<List<RequestMessageDTO>> getTeamRequestMessages(){
		return ResponseEntity.ok(trService.getSendedRequestMessages());
	}

	@DeleteMapping("/deleteTeamRequest/{id}")
	public ResponseEntity<HttpStatus> deleteTeamRequest(
			@PathVariable("id") Integer id){
		trService.deleteTeamRequest(id);
		return new ResponseEntity(HttpStatus.OK);
	}
}
