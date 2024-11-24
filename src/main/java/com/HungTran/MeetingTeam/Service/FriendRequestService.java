package com.HungTran.MeetingTeam.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Converter.RequestMessageConverter;
import com.HungTran.MeetingTeam.Converter.UserConverter;
import com.HungTran.MeetingTeam.DTO.RequestMessageDTO;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.FriendRelation;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.FriendRelationRepo;
import com.HungTran.MeetingTeam.Repository.RequestMessageRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Util.InfoChecking;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
	private final UserRepo userRepo;
	private final FriendRelationRepo frRepo;
	private final RequestMessageRepo requestMessRepo;
	private final InfoChecking infoChecking;
	private final RequestMessageConverter rmConverter;
	private final UserConverter userConverter;
	private final SocketTemplate socketTemplate;

	public void friendRequest(String email, String content) {
		User recipient=userRepo.findByEmail(email).orElseThrow(()->new RequestException("Sorry!!Double check that the email is correct"));
		var userId=infoChecking.getUserIdFromContext();
		if(recipient.getId().equals(userId))
			throw new RequestException("Hmn! It seems that the email you enter is your own");
		if(userRepo.havingFriend(userId,recipient.getId())>0)
			throw new RequestException("The owner of this email has already been your friend");
		var message= RequestMessage.builder()
						.sender(infoChecking.getUserFromContext())
						.recipient(recipient)
						.content(content)
						.createdAt(LocalDateTime.now())
						.build();
		socketTemplate.sendUser(userId,"/addFriendRequest",message);
		socketTemplate.sendUser(recipient.getId(),"/addFriendRequest",message);
		requestMessRepo.save(message);
	}
	public List<RequestMessageDTO> getFriendRequests() {
		var requests=requestMessRepo.getFriendRequests(infoChecking.getUserIdFromContext());
		return rmConverter.convertToDTO(requests);
	}
	public void deleteFriendRequest(Integer requestId) {
		var request=requestMessRepo.findById(requestId).orElseThrow(()->new RequestException("RequestId "+requestId+" does not exists"));
		requestMessRepo.deleteById(requestId);
		socketTemplate.sendUser(request.getSender().getId(),"/deleteFriendRequest",requestId);
		socketTemplate.sendUser(request.getRecipient().getId(),"/deleteFriendRequest",requestId);
	}
	@Transactional
	public void acceptFriend(Integer requestId) {
		User u=infoChecking.getUserFromContext();
		RequestMessage message=requestMessRepo.findById(requestId).orElseThrow(()->new RequestException("requestId "+requestId+" not found"));
		if(!u.getId().equals(message.getRecipient().getId()))
			throw new RequestException("If you want to make friend with someone, you need to send friend request to them");
		FriendRelation fr=frRepo.findByUsers(u, message.getSender());
		if(fr==null) fr=new FriendRelation(u,message.getSender(),"FRIEND");
		else fr.setStatus("FRIEND");
		frRepo.save(fr);
		socketTemplate.sendUser(message.getSender().getId(),"/updateFriends",userConverter.convertUserToDTO(u));
		socketTemplate.sendUser(message.getRecipient().getId(),"/updateFriends",userConverter.convertUserToDTO(message.getSender()));
		requestMessRepo.deleteById(requestId);
		socketTemplate.sendUser(message.getSender().getId(),"/deleteFriendRequest",requestId);
		socketTemplate.sendUser(message.getRecipient().getId(),"/deleteFriendRequest",requestId);
	}
}
