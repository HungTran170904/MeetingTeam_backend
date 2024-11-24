package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.DTO.MeetingDTO;
import com.HungTran.MeetingTeam.Model.Meeting;

@Component
public class MeetingConverter {
	@Autowired
	UserConverter userConverter;
	private ModelMapper modelMapper;
	public MeetingConverter() {
		modelMapper=new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}
	public MeetingDTO convertToDTO(Meeting meeting) {
		var dto= modelMapper.map(meeting,MeetingDTO.class);
		return dto;
	}
	public List<MeetingDTO> convertToDTOs(List<Meeting> meetings) {
		List<MeetingDTO> dtos=new ArrayList();
		for(Meeting meeting: meetings) {
			var dto=convertToDTO(meeting);
			dtos.add(dto);
		}
		return dtos;
	}
	public Meeting convertToMeeting(MeetingDTO dto) {
		return modelMapper.map(dto,Meeting.class);
	}
}
