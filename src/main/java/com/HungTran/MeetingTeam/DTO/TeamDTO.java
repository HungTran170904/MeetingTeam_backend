package com.HungTran.MeetingTeam.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.HungTran.MeetingTeam.Model.Channel;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.TeamMember;

import lombok.Data;

@Data
public class TeamDTO {
	private String id;
	private String teamName;
	private String urlIcon;
	private Boolean autoAddMember=false;
	private List<TeamMemberDTO> members;
	private List<ChannelDTO> channels;
}
