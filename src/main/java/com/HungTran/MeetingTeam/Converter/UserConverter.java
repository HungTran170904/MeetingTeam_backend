package com.HungTran.MeetingTeam.Converter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.User;

@Component
public class UserConverter {
	public User convertDTOToUser(UserDTO dto) {
		var u=new User();
		u.setId(dto.getId());
		u.setEmail(dto.getEmail());
		u.setPassword(dto.getPassword());
		u.setBirthday(dto.getBirthday());
		u.setPhoneNumber(dto.getPhoneNumber());
		u.setStatus(dto.getStatus());
		u.setUrlIcon(dto.getUrlIcon());
		u.setNickName(dto.getNickName());
		return u;
	}
	public UserDTO convertUserToDTO(User u) {
		var dto=new UserDTO();
		dto.setId(u.getId());
		dto.setEmail(u.getEmail());
		dto.setBirthday(u.getBirthday());
		dto.setStatus(u.getStatus());
		dto.setUrlIcon(u.getUrlIcon());
		dto.setPhoneNumber(u.getPhoneNumber());
		dto.setNickName(u.getNickName());
		dto.setRole(u.getRole().getRoleName());
		dto.setLastActive(u.getLastActive());
		dto.setCalendarMeetingIds(u.getCalendarMeetingIds());
		return dto;
	}
	public UserDTO convertUserToDTO(User u, String token) {
		var dto=convertUserToDTO(u);
		return dto;
	}
	public List<UserDTO> convertUserToDTO(List<User> users) {
		List<UserDTO> dtos=new ArrayList();
		for(User u: users) {
			dtos.add(convertUserToDTO(u));
		}
		return dtos;
	}
}
