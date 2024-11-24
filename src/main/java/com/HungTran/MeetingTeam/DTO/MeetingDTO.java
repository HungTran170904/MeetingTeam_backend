package com.HungTran.MeetingTeam.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.HungTran.MeetingTeam.Model.MessageReaction;
import com.HungTran.MeetingTeam.Model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDTO {
	private String id;
	private LocalDateTime createdAt;
	private Boolean isCanceled=false;
	private String title;
	private String channelId;
	private String creatorId;
	private Set<Integer> scheduledDaysOfWeek;
	private LocalDateTime scheduledTime;
	private LocalDateTime endDate;
	private List<MessageReaction> reactions;
	private Set<String> emailsReceivedNotification;
}
