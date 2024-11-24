package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.DTO.TeamDTO;
import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Model.Channel;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.TeamMember;
import com.HungTran.MeetingTeam.Model.User;

@Component
public class TeamConverter {
	@Autowired
	TeamMemberConverter tmConverter;
	@Autowired
	ChannelConverter channelConverter;
	public TeamDTO convertTeamToDTO(Team team) {
		var dto=new TeamDTO();
		dto.setId(team.getId());
		dto.setTeamName(team.getTeamName());
		dto.setUrlIcon(team.getUrlIcon());
		dto.setAutoAddMember(team.getAutoAddMember());
		return dto;
	}
	public TeamDTO convertTeamToDTO(Team team, List<TeamMember> members, List<Channel> channels) {
		var dto=convertTeamToDTO(team);
		dto.setMembers(tmConverter.convertToDTO(members));
		dto.setChannels(channelConverter.convertToDTO(channels));
		return dto;
	}
	public List<TeamDTO> convertToDTOs(List<Team> teams){
		List<TeamDTO> dtos=new ArrayList();
		for(Team team: teams) {
			dtos.add(convertTeamToDTO(team,team.getMembers(),team.getChannels()));
		}
		return dtos;
	}
	public Team convertDTOToTeam(TeamDTO dto) {
		var team=new Team();
		team.setId(dto.getId());
		team.setTeamName(dto.getTeamName());
		team.setAutoAddMember(dto.getAutoAddMember());
		team.setUrlIcon(dto.getUrlIcon());
		return team;
	}
}
