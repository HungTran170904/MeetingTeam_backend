package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.DTO.TeamMemberDTO;
import com.HungTran.MeetingTeam.Model.TeamMember;

@Component
public class TeamMemberConverter {
	@Autowired
	UserConverter userConverter;
	public List<TeamMemberDTO> convertToDTO(List<TeamMember> tms) {
		List<TeamMemberDTO> dtos=new ArrayList();
		for(TeamMember tm: tms) {
			TeamMemberDTO dto=new 
					TeamMemberDTO(userConverter.convertUserToDTO(tm.getU()), tm.getRole());
			dtos.add(dto);
		}
		return dtos;
	}
	public TeamMemberDTO convertToDTO(TeamMember tm) {
		var dto=new TeamMemberDTO(userConverter.convertUserToDTO(tm.getU()), tm.getRole());
		return dto;
	}
}
