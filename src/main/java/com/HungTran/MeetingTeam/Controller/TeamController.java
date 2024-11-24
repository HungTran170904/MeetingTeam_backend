package com.HungTran.MeetingTeam.Controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.HungTran.MeetingTeam.DTO.TeamDTO;
import com.HungTran.MeetingTeam.Service.TeamService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {
	private final TeamService teamService;
	private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

	@PostMapping("/createTeam")
	public ResponseEntity<TeamDTO> addTeam(
			@RequestBody TeamDTO team) {
		return ResponseEntity.ok(teamService.createTeam(team));
	}
	@GetMapping("/getJoinedTeams")
	public ResponseEntity<List<TeamDTO>> getJoinTeams() {
		return ResponseEntity.ok(teamService.getJoinedTeams());
	}
	@GetMapping("/leaveTeam/{teamId}")
	public ResponseEntity<HttpStatus> leaveTeam(
			@PathVariable("teamId") String teamId){
		teamService.leaveTeam(teamId);
		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/addFriendsToTeam")
	public ResponseEntity<HttpStatus> addFriendsToTeam(
			@RequestParam("friendIds") String friendIdsJson,
			@RequestParam("teamId") String teamId) throws Exception{
		List<String> friendIds=objectMapper.readValue(friendIdsJson,new TypeReference<List<String>>(){});
		teamService.addFriendsToTeam(friendIds, teamId);
		return new ResponseEntity(HttpStatus.OK);
	}
	@GetMapping("/kickMember")
	public ResponseEntity<HttpStatus> kickMember(
			@RequestParam("teamId") String teamId,
			@RequestParam("memberId") String memberId){
		teamService.kickMember(teamId, memberId);
		return new ResponseEntity(HttpStatus.OK);
	}
	@PutMapping("/updateTeam")
	public ResponseEntity<HttpStatus> setAutoAddMember(
			@RequestParam("teamDTO") String teamJson,
			@RequestParam(name="file",required=false) MultipartFile file) throws Exception{
		TeamDTO dto=objectMapper.readValue(teamJson,TeamDTO.class);
		teamService.updateTeam(dto, file);
		return new ResponseEntity(HttpStatus.OK);
	}
}
