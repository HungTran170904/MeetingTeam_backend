package com.HungTran.MeetingTeam.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HungTran.MeetingTeam.Model.Message;

import jakarta.transaction.Transactional;
@Repository
public interface MessageRepo extends JpaRepository<Message, Integer>{
	@Query("select m from Message m where m.channelId=?1 order by m.createdAt DESC")
	public List<Message> getMessagesByChannelId(String channelId, Pageable pageable);
	 
	@Query("select m from Message m where m.channelId=?1 and m.messageType in ('IMAGE','AUDIO','VIDEO','FILE')")
	public List<Message> getFileMessagesByChannelId(String channelId);
	
	@Query("select m from Message m where m.recipientId=?1 and m.messageType in ('IMAGE','AUDIO','VIDEO','FILE')")
	public List<Message> getFileMessagesByRecipientId(String recipientId);
	
	@Query("select m from Message m where (m.senderId=?1 and m.recipientId=?2) or (m.senderId=?2 and m.recipientId=?1) order by m.createdAt DESC")
	public List<Message> getPrivateMessages(String userId1, String userId2, Pageable pageable);
	 
	@Modifying
    @Transactional
    public void deleteByChannelId(String channelId);
	
	@Modifying
    @Transactional
    public void deleteByRecipientId(String recipientId);
}
