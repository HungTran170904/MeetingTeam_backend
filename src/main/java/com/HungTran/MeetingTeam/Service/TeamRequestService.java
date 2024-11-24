package com.HungTran.MeetingTeam.Service;

import java.util.List;

import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Converter.RequestMessageConverter;
import com.HungTran.MeetingTeam.Converter.TeamConverter;
import com.HungTran.MeetingTeam.Converter.TeamMemberConverter;
import com.HungTran.MeetingTeam.DTO.RequestMessageDTO;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.TeamMember;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.RequestMessageRepo;
import com.HungTran.MeetingTeam.Repository.TeamMemberRepo;
import com.HungTran.MeetingTeam.Repository.TeamRepo;
import com.HungTran.MeetingTeam.Util.InfoChecking;

@Service
@RequiredArgsConstructor
public class TeamRequestService {
	private final TeamRepo teamRepo;
	private final RequestMessageRepo requestMessRepo;
	private final TeamMemberRepo teamMemberRepo;
	private final TeamMemberConverter tmConverter;
	private final TeamConverter teamConverter;
	private final SocketTemplate socketTemplate;
	private final InfoChecking infoChecking;
	private final RequestMessageConverter rmConverter;

	public List<RequestMessageDTO> getTeamRequestMessages(String teamId) {
		var messages= requestMessRepo.getTeamRequestMessages(teamId);
		return rmConverter.convertToDTO(messages);
	}
	public List<RequestMessageDTO> getSendedRequestMessages() {
		var messages= requestMessRepo.getSendedRequestMessages(infoChecking.getUserIdFromContext());
		return rmConverter.convertToDTO(messages);
	}
	public String requestToJoinTeam(RequestMessage message) {
		User u=infoChecking.getUserFromContext();
		Team team=teamRepo.findById(message.getTeam().getId()).orElseThrow(()->new RequestException("TeamId does not exists"));
		if(team.getAutoAddMember()) {
			var tm=teamMemberRepo.findByTeamIdAndUserId(team.getId(),u.getId());
			if(tm==null) tm=new TeamMember(u, team,"MEMBER");
			else tm.setRole("MEMBER");
			teamMemberRepo.save(tm);
			socketTemplate.sendTeam(team.getId(),"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
			team=teamRepo.getTeamWithChannels(team.getId());
			team=teamRepo.getTeamWithChannels(team.getId());
			socketTemplate.sendUser(u.getId(),"/addTeam",
					teamConverter.convertTeamToDTO(team,team.getMembers(),team.getChannels()));
			return "You has been added to team '"+team.getTeamName()+"'";
		}
		else if(!requestMessRepo.existsBySenderAndTeam(u,message.getTeam())) {	
			message.setSender(u);
			requestMessRepo.save(message);
			return "Request has been sent successfully";
		}
		return "Request has been sent before! Please wait for admin of the team accepts";
	}
	public void acceptNewMember(String teamId,Integer messageId) {
		String userId=infoChecking.getUserIdFromContext();
		String role=teamMemberRepo.getRoleByUserIdAndTeamId(userId, teamId);
		if(role!=null&&role.equals("LEADER")||role.equals("DEPUTY")) {
			RequestMessage joinMessage=requestMessRepo.findById(messageId).orElseThrow(()->new RequestException("MessageId "+messageId+" not found"));
			var sender=joinMessage.getSender();
			var tm=teamMemberRepo.findByTeamIdAndUserId(teamId,sender.getId());
			if(tm==null) tm=new TeamMember(sender,teamRepo.getById(teamId),"MEMBER");
			else tm.setRole("MEMBER");
			teamMemberRepo.save(tm);
			if(sender.getStatus().equals("ONLINE")) {
				var team=teamRepo.getTeamWithChannels(teamId);
				team=teamRepo.getTeamWithChannels(teamId);
				socketTemplate.sendUser(sender.getId(),"/updateTeam",
						teamConverter.convertTeamToDTO(team,team.getMembers(),team.getChannels()));
			}
			socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
			requestMessRepo.deleteById(messageId);
		}
		else throw new RequestException("You do not have permission to add a new member!Contact leader or deputies of your team for help!");
	}
	public void deleteTeamRequest(Integer messageId) {
		requestMessRepo.deleteById(messageId);
	}
}
