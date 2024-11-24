package com.HungTran.MeetingTeam.DTO;

import java.time.LocalDateTime;

import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.Team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMessageDTO {
	private Integer id;
	private UserDTO sender; 
	private UserDTO recipient;
	private TeamDTO team;
	private String content;
	private LocalDateTime createdAt;
}
