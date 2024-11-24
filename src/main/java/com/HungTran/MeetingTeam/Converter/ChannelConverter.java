package com.HungTran.MeetingTeam.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.DTO.ChannelDTO;
import com.HungTran.MeetingTeam.Model.Channel;

@Component
public class ChannelConverter {
	public ChannelDTO convertToDTO(Channel channel) {
		ChannelDTO dto=new ChannelDTO();
		dto.setId(channel.getId());
		dto.setChannelName(channel.getChannelName());
		dto.setType(channel.getType());
		dto.setDescription(channel.getDescription());
		return dto;
	}
	public List<ChannelDTO> convertToDTO(List<Channel> channels){
		List<ChannelDTO> dtos=new ArrayList();
		for(Channel channel: channels) {
			dtos.add(convertToDTO(channel));
		}
		return dtos;
	}
	public Channel convertToChannel(ChannelDTO dto) {
		Channel channel=new Channel();
		channel.setId(dto.getId());
		channel.setChannelName(dto.getChannelName());
		channel.setDescription(dto.getDescription());
		channel.setType(dto.getType());
		return channel;
	}
}
