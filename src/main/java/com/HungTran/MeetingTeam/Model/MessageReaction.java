package com.HungTran.MeetingTeam.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageReaction {
	private String userId;
	private String emojiCode;
}
