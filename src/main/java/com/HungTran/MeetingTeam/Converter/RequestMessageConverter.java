package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.DTO.RequestMessageDTO;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.User;

@Component
public class RequestMessageConverter {
	@Autowired
	UserConverter userConveter;
	@Autowired
	TeamConverter teamConverter;
	public RequestMessage convertToRequestMessage(RequestMessageDTO dto) {
		var message=new RequestMessage();
		if(dto.getRecipient()!=null)
			message.setRecipient(new User(dto.getRecipient().getId()));
		if(dto.getTeam()!=null)
			message.setTeam(new Team(dto.getTeam().getId()));
		message.setContent(dto.getContent());
		message.setCreatedAt(dto.getCreatedAt());
		return message;
	}
	public RequestMessageDTO convertToDTO(RequestMessage message) {
		var dto=new RequestMessageDTO();
		dto.setId(message.getId());
		dto.setSender(userConveter.convertUserToDTO(message.getSender()));
		if(message.getRecipient()!=null)
			dto.setRecipient(userConveter.convertUserToDTO(message.getRecipient()));
		if(message.getTeam()!=null)
			dto.setTeam(teamConverter.convertTeamToDTO(message.getTeam()));
		dto.setContent(message.getContent());
		dto.setCreatedAt(message.getCreatedAt());
		return dto;
	}
	public List<RequestMessageDTO> convertToDTO(List<RequestMessage> messages) {
		var dtos=new ArrayList<RequestMessageDTO>();
		for(RequestMessage message: messages) {
			dtos.add(convertToDTO(message));
		}
		return dtos;
	}
}
