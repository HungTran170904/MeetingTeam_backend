package com.HungTran.MeetingTeam.Service;

import com.HungTran.MeetingTeam.Converter.MeetingConverter;
import com.HungTran.MeetingTeam.Repository.ChannelRepo;
import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.DateTimeUtil;
import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Repository.MeetingRepo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ZegoCloudService {
	private final MeetingRepo meetingRepo;
	private final ChannelRepo channelRepo;
	private final MeetingConverter meetingConverter;
	private final SocketTemplate socketTemplate;
	private final DateTimeUtil dateTimeUtil;

	public void createRoomNotification(String roomId) {
		var meeting=meetingRepo.findById(roomId).orElseThrow(()->new RequestException("RoomId "+roomId+" does not exists"));
		String teamId= channelRepo.findTeamIdById(meeting.getChannelId());
		meeting.setIsActive(true);
		socketTemplate.sendTeam(teamId,"/updateMeetings",meetingConverter.convertToDTO(meeting));
		meetingRepo.save(meeting);
	}
	public void closeRoomNotification(String roomId) {
		var meeting=meetingRepo.findById(roomId).orElseThrow(()->new RequestException("RoomId "+roomId+" does not exists"));
		String teamId= channelRepo.findTeamIdById(meeting.getChannelId());
		meeting.setIsActive(false);
		socketTemplate.sendTeam(teamId,"/updateMeetings",meetingConverter.convertToDTO(meeting));
		meetingRepo.save(meeting);
	}
}
