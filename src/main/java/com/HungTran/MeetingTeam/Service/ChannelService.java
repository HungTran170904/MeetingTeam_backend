package com.HungTran.MeetingTeam.Service;

import java.util.List;

import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Converter.ChannelConverter;
import com.HungTran.MeetingTeam.DTO.ChannelDTO;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.Channel;
import com.HungTran.MeetingTeam.Repository.ChannelRepo;
import com.HungTran.MeetingTeam.Repository.TeamMemberRepo;
import com.HungTran.MeetingTeam.Repository.TeamRepo;
import com.HungTran.MeetingTeam.Util.InfoChecking;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {
	private final ChannelRepo channelRepo;
	private final TeamRepo teamRepo;
	private final TeamMemberRepo teamMemberRepo;
	private final ChannelConverter channelConverter;
	private final InfoChecking infoChecking;
	private final SocketTemplate socketTemplate;
    private final UserRepo userRepo;
    private final ChatService chatService;

	public void updateChannel(ChannelDTO dto) {
		if(dto.getTeamId()==null) throw new RequestException("TeamId is required!");
		String userId=infoChecking.getUserIdFromContext();
		String role=teamMemberRepo.getRoleByUserIdAndTeamId(userId,dto.getTeamId());
		Channel channel=null;
		if(role.equals("LEADER")||role.equals("DEPUTY")) {
			if(dto.getId()!=null){
				channel=channelRepo.findById(channel.getId()).orElseThrow(()->new RequestException("Channel Id "+dto.getId()+" does not exists"));
				channel.setChannelName(dto.getChannelName());
				channel.setDescription(dto.getDescription());
			}
			else{
				channel=channelConverter.convertToChannel(dto);
				channel.setTeam(teamRepo.getById(dto.getTeamId()));
			}
			var savedChannel=channelRepo.save(channel);
			socketTemplate.sendTeam(dto.getTeamId(),"/updateChannels",channelConverter.convertToDTO(savedChannel));
		}
		else throw new RequestException("You do not have permission to add a new channel!Contact leader or deputies of your team for help!");
	}
	@Transactional
	public void deleteChannel(String channelId) {
		Channel channel=channelRepo.findById(channelId).orElseThrow(()->new RequestException("ChannelId "+channelId+" does not exists"));
		var teamId=channelRepo.findTeamIdById(channelId);
		if(channel.getType().equals(Constraint.VOICE_CHANNEL)) {
			List<User> users=teamMemberRepo.findUsersByTeamId(teamId);
			List<String> meetingIds=channelRepo.getMeetingIdsById(channelId);
			for(var user:users)
				user.getCalendarMeetingIds().removeAll(meetingIds);
			userRepo.saveAll(users);
		}
		chatService.deleteMessagesByChannelId(channelId);
		channelRepo.deleteById(channelId);
		socketTemplate.sendTeam(channel.getTeam().getId(),"/removeChannel", channelId);
	}
}
