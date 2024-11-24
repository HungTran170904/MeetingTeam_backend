package com.HungTran.MeetingTeam.DTO;

import java.util.List;

import com.HungTran.MeetingTeam.Model.Message;

import lombok.Data;

@Data
public class ChannelDTO {
	private String id;
	private String channelName;
	private String type;
	private String description;
	private String teamId;
}
