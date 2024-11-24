package com.HungTran.MeetingTeam.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDTO {
	private UserDTO u;
	private String role;
}
