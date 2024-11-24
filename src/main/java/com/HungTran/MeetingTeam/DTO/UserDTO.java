package com.HungTran.MeetingTeam.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.HungTran.MeetingTeam.Model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
@Data
public class UserDTO {
	private String id;
	private String email;
	private String password;
	private String urlIcon;
	private String nickName;
	private String phoneNumber;
	private LocalDate birthday;
	private String status; //ONLINE, OFFLINE
	private String role;
	private LocalDateTime lastActive;
	private Set<String> calendarMeetingIds;
}
