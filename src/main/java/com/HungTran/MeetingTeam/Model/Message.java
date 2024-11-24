package com.HungTran.MeetingTeam.Model;

import java.time.LocalDateTime;
import java.util.List;

import com.HungTran.MeetingTeam.Converter.VotingConverter;
import com.HungTran.MeetingTeam.Converter.ReactionConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column(columnDefinition = "TEXT")
	private String content;
	private LocalDateTime createdAt;
	private String senderId;
	private String channelId; //teamId cho th JOINREQUEST
	private String recipientId;
	private String messageType; // UNSEND,TEXT, FILE, IMAGE, VIDEO, AUDIO
	private String parentMessageId; 
	private String fileName;

	@Column(columnDefinition = "TEXT")
	@Convert(converter=ReactionConverter.class)
	private List<MessageReaction> reactions;

	@Column(columnDefinition = "TEXT")
	@Convert(converter= VotingConverter.class)
	private Voting voting;
}
