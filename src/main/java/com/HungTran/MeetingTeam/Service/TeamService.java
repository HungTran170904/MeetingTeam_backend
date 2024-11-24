package com.HungTran.MeetingTeam.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HungTran.MeetingTeam.Converter.RequestMessageConverter;
import com.HungTran.MeetingTeam.Converter.TeamConverter;
import com.HungTran.MeetingTeam.Converter.TeamMemberConverter;
import com.HungTran.MeetingTeam.Converter.UserConverter;
import com.HungTran.MeetingTeam.DTO.RequestMessageDTO;
import com.HungTran.MeetingTeam.DTO.TeamDTO;
import com.HungTran.MeetingTeam.DTO.TeamMemberDTO;
import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Exception.PermissionException;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.Channel;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.TeamMember;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.ChannelRepo;
import com.HungTran.MeetingTeam.Repository.MessageRepo;
import com.HungTran.MeetingTeam.Repository.RequestMessageRepo;
import com.HungTran.MeetingTeam.Repository.TeamMemberRepo;
import com.HungTran.MeetingTeam.Repository.TeamRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.InfoChecking;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {
	private final TeamRepo teamRepo;
	private final UserRepo userRepo;
	private final ChannelRepo channelRepo;
	private final TeamMemberRepo teamMemberRepo;
	private final TeamConverter teamConverter;
	private final InfoChecking infoChecking;
	private final CloudinaryService cloudinaryService;
	private final SocketTemplate socketTemplate;
	private final TeamMemberConverter tmConverter;

	@Transactional
	public TeamDTO createTeam(TeamDTO dto) {
		User leader=infoChecking.getUserFromContext();
		Team team=teamConverter.convertDTOToTeam(dto);
		team.setAutoAddMember(false);
		var savedTeam=teamRepo.save(team);
		var member=teamMemberRepo.save(new TeamMember(leader, savedTeam, "LEADER"));
		var generalChannel=Channel.builder()
				.team(savedTeam)
				.channelName("General")
				.type(Constraint.TEXT_CHANNEL)
				.build();
		channelRepo.save(generalChannel);
		return teamConverter.convertTeamToDTO(team,List.of(member), List.of(generalChannel));
	}
	@Transactional
	public void addFriendsToTeam(List<String> friendIds, String teamId) {
		User u=infoChecking.getUserFromContext();
		if(!teamMemberRepo.existsByTeamAndU(teamRepo.getById(teamId), u))
			throw new PermissionException("You do not have permissions to add new members to this team");
		var tms=new ArrayList<TeamMember>();
		for(String friendId: friendIds) {
			TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId, friendId);
			if(tm==null)tm=new TeamMember(userRepo.getById(friendId),teamRepo.getById(teamId),"MEMBER");
			else if(tm.getRole().equals("LEAVE")) tm.setRole("MEMBER");
			tms.add(tm);
		}
		List<TeamMember> savedTMs=teamMemberRepo.saveAll(tms);
		socketTemplate.sendTeam(teamId,"/updateMembers",tmConverter.convertToDTO(savedTMs));
		Team team=teamRepo.getTeamWithMembers(teamId);
		team=teamRepo.getTeamWithChannels(teamId);
		for(String friendId: friendIds) 
			socketTemplate.sendUser(friendId,"/addTeam",
				teamConverter.convertTeamToDTO(team,team.getMembers(),team.getChannels()));
	}
	public List<TeamDTO> getJoinedTeams(){
		List<String> teamIds = teamRepo.getTeamIdsByUserId(infoChecking.getUserIdFromContext());
		List<Team> teams=teamRepo.getTeamsWithMembers(teamIds);
		teams=teamRepo.getTeamsWithChannels(teamIds);
		return teamConverter.convertToDTOs(teams);
	}
	public void leaveTeam(String teamId) {
		String userId=infoChecking.getUserIdFromContext();
		TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId, userId);
		tm.setRole("LEAVE");
		teamMemberRepo.save(tm);
		socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
	}
	public void kickMember(String teamId, String memberId) {
		User u=infoChecking.getUserFromContext();
		String role=teamMemberRepo.getRoleByUserIdAndTeamId(u.getId(), teamId);
		TeamMember tm=teamMemberRepo.findByTeamIdAndUserId(teamId,memberId);
		if(role.equals("LEADER")) {
			tm.setRole("LEAVE");
			teamMemberRepo.save(tm);
			socketTemplate.sendUser(memberId,"/deleteTeam",teamId);
			socketTemplate.sendTeam(teamId,"/updateMembers",List.of(tmConverter.convertToDTO(tm)));
		}
		else throw new RequestException("You do not have permission to kick a member!Contact leader or deputies of your team for help!");
	}
	public void updateTeam(TeamDTO dto,MultipartFile file) {
		Team team=teamConverter.convertDTOToTeam(dto);
		if(team.getId()==null) throw new RequestException("TeamId must not be null");
		if(file!=null) {
			String url=cloudinaryService.uploadFile(file,team.getId(),team.getUrlIcon());
			team.setUrlIcon(url);
		}
		Team savedTeam=teamRepo.save(team);
		socketTemplate.sendTeam(team.getId(),"/updateTeam",teamConverter.convertTeamToDTO(savedTeam));
	}
}
