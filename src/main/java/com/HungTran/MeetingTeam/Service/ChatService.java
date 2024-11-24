package com.HungTran.MeetingTeam.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Exception.MessageException;
import com.HungTran.MeetingTeam.Exception.PermissionException;
import com.HungTran.MeetingTeam.Model.Channel;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.MessageReaction;
import com.HungTran.MeetingTeam.Repository.ChannelRepo;
import com.HungTran.MeetingTeam.Repository.MessageRepo;
import com.HungTran.MeetingTeam.Repository.TeamMemberRepo;
import com.HungTran.MeetingTeam.Repository.TeamRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Util.InfoChecking;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final MessageRepo messageRepo;
	private final TeamRepo teamRepo;
	private final UserRepo userRepo;
	private final ChannelRepo channelRepo;
	private final TeamMemberRepo teamMemberRepo;
	private final SocketTemplate socketTemplate;
	private final CloudinaryService cloudinaryService;
	private final InfoChecking infoChecking;

	public void broadcastMessage(Message message) {
		if(message.getRecipientId()!=null) {
			socketTemplate.sendUser(message.getRecipientId(),"/messages", message);
			socketTemplate.sendUser(message.getSenderId(),"/messages", message);
		}
		else if(message.getChannelId()!=null) {
			String teamId=channelRepo.findTeamIdById(message.getChannelId());
			socketTemplate.sendTeam(teamId,"/messages", message);
			socketTemplate.sendTeam(teamId,"/messages",message);
		}
	}
	public void receivePublicChatMessage(Message chatMessage, MultipartFile file) {
		String teamId=channelRepo.findTeamIdById(chatMessage.getChannelId());
		chatMessage.setSenderId(infoChecking.getUserIdFromContext());
		if(file!=null) {
			chatMessage.setContent(cloudinaryService.uploadFile(file,infoChecking.getUserIdFromContext(),null));
			String type=file.getContentType().split("/")[0];
			if(type.equals("image")) chatMessage.setMessageType(Constraint.IMAGE);
			else if(type.equals("video")) chatMessage.setMessageType(Constraint.VIDEO);
			else if(type.equals("audio")) chatMessage.setMessageType(Constraint.AUDIO);
			else chatMessage.setMessageType("FILE");
			chatMessage.setFileName(file.getOriginalFilename());
		}
		var savedMess=messageRepo.save(chatMessage);
		socketTemplate.sendTeam(teamId,"/messages",savedMess);
	}
	
	public void receivePrivateChatMessage(Message chatMessage, MultipartFile file) {
		if(chatMessage.getRecipientId()==null) 
			throw new MessageException("RecipientId is not null");
		chatMessage.setSenderId(infoChecking.getUserIdFromContext());
		if(file!=null) {
			chatMessage.setContent(cloudinaryService.uploadFile(file,infoChecking.getUserIdFromContext(),null));
			String type=file.getContentType().split("/")[0];
			if(type.equals("image")) chatMessage.setMessageType(Constraint.IMAGE);
			else if(type.equals("video")) chatMessage.setMessageType(Constraint.VIDEO);
			else if(type.equals("audio")) chatMessage.setMessageType(Constraint.AUDIO);
			else chatMessage.setMessageType(Constraint.FILE);
			chatMessage.setFileName(file.getOriginalFilename());
		}
		var savedMess=messageRepo.save(chatMessage);
		socketTemplate.sendUser(savedMess.getRecipientId(),"/messages", savedMess);
		socketTemplate.sendUser(savedMess.getSenderId(),"/messages", savedMess);
	}
	
	public List<Message> getTextChannelMessages(Integer receivedMessageNum, String channelId) {
		Channel channel=channelRepo.findById(channelId).orElseThrow(()->new RequestException("ChannelId "+channelId+" does not exist"));
		if(!teamMemberRepo.existsByTeamAndU(channel.getTeam(),infoChecking.getUserFromContext()))
			throw new PermissionException("You do not have permission to read messages from the required conversation");
		int pageSize=infoChecking.findBestPageSize(receivedMessageNum);
		PageRequest pagination=PageRequest.of(receivedMessageNum/pageSize,pageSize);
		var result= messageRepo.getMessagesByChannelId(channelId, pagination);
		Collections.reverse(result);
		return result;
	}
	
	public List<Message> getPrivateMessages(Integer receivedMessageNum, String friendId){
		if(userRepo.havingFriend(infoChecking.getUserIdFromContext(),friendId)==0)
			throw new PermissionException("You do not have permission to read messages from the given person");
		int pageSize=infoChecking.findBestPageSize(receivedMessageNum);
		PageRequest pagination=PageRequest.of(receivedMessageNum/pageSize,pageSize);
		var result= messageRepo.getPrivateMessages(infoChecking.getUserIdFromContext(),friendId, pagination);
		Collections.reverse(result);
		return result;
	}
	
	public void reactMessage(Integer messageId, MessageReaction reaction) {
		String userId=infoChecking.getUserIdFromContext();
		var message=messageRepo.findById(messageId).orElseThrow(()-> new MessageException(("MessageId "+messageId+" not found")));
		var reactions=message.getReactions();
		if(reactions==null) reactions=new ArrayList();
		int i=0;
		for(;i<reactions.size(); i++) {
			if(reactions.get(i).getUserId().equals(userId)) {
				if(reaction.getEmojiCode()==null) reactions.remove(i);
				else reactions.set(i, reaction);
				break;
			}
		}
		if(i==reactions.size()&&reaction.getEmojiCode()!=null) 
			reactions.add(reaction);
		broadcastMessage(message);
		message.setReactions(reactions);
		messageRepo.save(message);
	}
	
	public void unsendMessage(Integer messageId) {
		Message message=messageRepo.findById(messageId)
				.orElseThrow(()->new RequestException("Message id "+messageId+" not found!"));
		String type=message.getMessageType();
		if(type==Constraint.IMAGE||type==Constraint.AUDIO||type==Constraint.VIDEO||type==Constraint.FILE) {
			cloudinaryService.deleteFile(message.getContent());
		}
		message.setMessageType(Constraint.UNSEND);
		message.setContent(null);
		message.setReactions(null);
		message.setVoting(null);
		broadcastMessage(message);
		messageRepo.save(message);
	}
	public void deleteMessagesByChannelId(String channelId) {
		for(Message message: messageRepo.getFileMessagesByChannelId(channelId)) {
			cloudinaryService.deleteFile(message.getContent());
		}
		messageRepo.deleteByChannelId(channelId);
	}
}