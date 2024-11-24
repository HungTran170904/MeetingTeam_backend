package com.HungTran.MeetingTeam.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.HungTran.MeetingTeam.Converter.UserConverter;
import com.HungTran.MeetingTeam.DTO.CalendarDTO;
import com.HungTran.MeetingTeam.Util.DateTimeUtil;
import com.HungTran.MeetingTeam.Util.SocketTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Converter.MeetingConverter;
import com.HungTran.MeetingTeam.DTO.MeetingDTO;
import com.HungTran.MeetingTeam.Exception.PermissionException;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.Meeting;
import com.HungTran.MeetingTeam.Model.MessageReaction;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.ChannelRepo;
import com.HungTran.MeetingTeam.Repository.MeetingRepo;
import com.HungTran.MeetingTeam.Repository.TeamMemberRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Util.InfoChecking;
import com.HungTran.MeetingTeam.Util.ZegoToken;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {
	private final RabbitMQService rabbitMQService;
	private final ZegoToken zegoToken;
	private final InfoChecking infoChecking;
	private final MeetingConverter meetingConverter;
	private final SocketTemplate socketTemplate;
	private final ChannelRepo channelRepo;
	private final MeetingRepo meetingRepo;
	private final TeamMemberRepo tmRepo;
	private final UserRepo userRepo;
	private final UserConverter userConverter;
	private final DateTimeUtil dateTimeUtil;
	@Value("${zegocloud.app-id}")
	String zegoAppId;
	private ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

	public ObjectNode generateToken(String meetingId) {
		var meeting=meetingRepo.findById(meetingId).orElseThrow(()->new RequestException("MeetingId "+meetingId+" does not exists"));
		if(meeting.getIsCanceled()!=null&&meeting.getIsCanceled()) throw new RequestException("This meeting has been closed");
		User u=infoChecking.getUserFromContext();
		if(tmRepo.existsByChannelIdAndU(u,meeting.getChannelId())==0)
			throw new PermissionException("You do not have permission to get token from this meeting");
		String token= zegoToken.generateToken(infoChecking.getUserIdFromContext(), meetingId);
		try{
			ObjectNode jsonObject= objectMapper.createObjectNode();
			jsonObject.put("token", token);
			jsonObject.put("appId",zegoAppId);
			var userDTO=userConverter.convertUserToDTO(u);
			String userJson = objectMapper.writeValueAsString(userDTO);
			jsonObject.put("user",userJson);
			return jsonObject;
		}
		catch(Exception ex){
			throw new RequestException("Can not parse object to json string");
		}
	}
	public List<MeetingDTO> getVideoChannelMeetings(String channelId, Integer receivedMeetingNum){
		int pageSize=infoChecking.findBestPageSize(receivedMeetingNum);
		var meetings= meetingRepo.getMeetingsByChannelId(channelId,PageRequest.of(receivedMeetingNum/pageSize, pageSize));
		Collections.reverse(meetings);
		return meetingConverter.convertToDTOs(meetings);
	}
	public String createMeeting(MeetingDTO dto) {
		Meeting meeting=meetingConverter.convertToMeeting(dto);
		User u=infoChecking.getUserFromContext();
		meeting.setCreatorId(u.getId());
		meeting.setIsCanceled(false);
		Team team=channelRepo.findTeamById(meeting.getChannelId());
		if(team==null) throw new RequestException("ChannelId "+meeting.getChannelId()+" is invalid");
		if(!tmRepo.existsByTeamAndU(team, u))
			throw new PermissionException("You do not have permission to create a meeting from this team");
		meeting=meetingRepo.save(meeting);
		if(meeting.getScheduledTime()!=null) {
			rabbitMQService.sendAddedTaskMessage(meeting);
		}
		socketTemplate.sendTeam(team.getId(),"/updateMeetings",meetingConverter.convertToDTO(meeting));
		return meeting.getId();
	}
	public void updateMeeting(MeetingDTO dto) {
		var meeting=meetingRepo.findById(dto.getId()).orElseThrow(()->new RequestException("MeetingId "+dto.getId()+" does not exists"));
		if(meeting.getIsCanceled())
			throw new RequestException("This meeting was canceled");
		if(!infoChecking.getUserIdFromContext().equals(meeting.getCreatorId()))
			throw new PermissionException("You do not have permission to update this meeting");
		if(dto.getScheduledTime()==null) throw new RequestException("Scheduled Time must not be null");

		Team team=channelRepo.findTeamById(meeting.getChannelId());
		meeting.setScheduledTime(dto.getScheduledTime());
		meeting.setScheduledDaysOfWeek(dto.getScheduledDaysOfWeek());
		meeting.setEndDate(dto.getEndDate());
		meeting.setTitle(dto.getTitle());

		rabbitMQService.sendAddedTaskMessage(meeting);
		socketTemplate.sendTeam(team.getId(),"/updateMeetings",meetingConverter.convertToDTO(meeting));
		meetingRepo.save(meeting);
	}
	public void reactMeeting(String meetingId, MessageReaction reaction) {
		var meeting=meetingRepo.findById(meetingId).orElseThrow(()->new RequestException("MeetingId "+meetingId+" does not exists"));
		var reactions=meeting.getReactions();
		if(reactions==null) reactions=new ArrayList();
		int i=0;
		for(;i<reactions.size();i++) {
			if(reaction.getUserId().equals(reactions.get(i).getUserId())) {
				if(reaction.getEmojiCode()==null) reactions.remove(i);
				else reactions.set(i, reaction);
				break;
			}
		}
		if(i==reactions.size()&&reaction.getEmojiCode()!=null) 
			reactions.add(reaction);
		meeting.setReactions(reactions);
		String teamId=channelRepo.findTeamIdById(meeting.getChannelId());
		socketTemplate.sendTeam(teamId,"/updateMeetings",meetingConverter.convertToDTO(meeting));
		meetingRepo.save(meeting);
	}
	public void cancelMeeting(String meetingId) {
		var meeting=meetingRepo.findById(meetingId).orElseThrow(()->new RequestException("MeetingId "+meetingId+" does not exists"));
		if(!infoChecking.getUserIdFromContext().equals(meeting.getCreatorId()))
			throw new PermissionException("You do not have permission to update this meeting");
		Team team=channelRepo.findTeamById(meeting.getChannelId());
		if(!meeting.getIsCanceled()) {
			rabbitMQService.sendRemovedTaskMessage(meeting.getId());
			meeting.setIsCanceled(true);
		}
		else {
			rabbitMQService.sendAddedTaskMessage(meeting);
			meeting.setIsCanceled(false);
		}
		socketTemplate.sendTeam(team.getId(),"/updateMeetings",meetingConverter.convertToDTO(meeting));
		meetingRepo.save(meeting);
	}
	public void registerEmailNotification(String meetingId, boolean receiveEmail) {
		var meeting=meetingRepo.findById(meetingId).orElseThrow(()->new RequestException("MeetingId "+meetingId+" does not exists"));
		if(receiveEmail) meeting.getEmailsReceivedNotification().add(infoChecking.getUserFromContext().getEmail());
		else meeting.getEmailsReceivedNotification().remove(infoChecking.getUserFromContext().getEmail());
		meetingRepo.save(meeting);
	}
	public void addToCalendar(String meetingId, boolean isAdded) {
		User u=infoChecking.getUserFromContext();
		if(isAdded) u.getCalendarMeetingIds().add(meetingId);
		else u.getCalendarMeetingIds().remove(meetingId);
		userRepo.save(u);
	}
	public CalendarDTO getMeetingsOfWeek(Integer week){
		var weekRange= dateTimeUtil.getWeekRange(week);
		var user=infoChecking.getUserFromContext();
		var rawMeetings=meetingRepo.getByIds(user.getCalendarMeetingIds());
		var meetings=rawMeetings.stream().filter(m->{
			if(m.getScheduledDaysOfWeek()==null||m.getScheduledDaysOfWeek().isEmpty()){
				return m.getScheduledTime().isAfter(weekRange.get(0))&&
						m.getScheduledTime().isBefore(weekRange.get(1));
			}
			else{
				if(m.getScheduledTime().isAfter(weekRange.get(1))) return false;
				if(m.getEndDate()!=null&&m.getEndDate().isBefore(weekRange.get(0))) return false;
				return true;
			}
		}).toList();
		var calendarDTO=new CalendarDTO(meetingConverter.convertToDTOs(meetings),weekRange);
		return calendarDTO;
	}
	@Transactional
	public void deleteMeeting(String meetingId) {
		var meeting=meetingRepo.findById(meetingId).orElseThrow(()->new RequestException("MeetingId "+meetingId+" does not exists"));
		if(!infoChecking.getUserIdFromContext().equals(meeting.getCreatorId()))
			throw new PermissionException("You do not have permission to delete this meeting");
		String teamId=channelRepo.findTeamIdById(meeting.getChannelId());
		Map<String, String> map=new HashMap();
		map.put("channelId", meeting.getChannelId());
		map.put("meetingId",meetingId);
		socketTemplate.sendTeam(teamId,"/deleteMeeting", map);
		rabbitMQService.sendRemovedTaskMessage(meeting.getId());
		meetingRepo.deleteById(meetingId);
		var user=infoChecking.getUserFromContext();
		if(user.getCalendarMeetingIds().contains(meetingId)) {
			user.getCalendarMeetingIds().remove(meetingId);
			userRepo.save(user);
		}
	}
}
