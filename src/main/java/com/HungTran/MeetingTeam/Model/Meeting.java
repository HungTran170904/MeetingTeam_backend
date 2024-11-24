package com.HungTran.MeetingTeam.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import com.HungTran.MeetingTeam.Converter.SetStringConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.HungTran.MeetingTeam.Converter.IntegerSetConverter;
import com.HungTran.MeetingTeam.Converter.ReactionConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Meeting {
	@Id
	@UuidGenerator
	private String id;
	private LocalDateTime createdAt;
	@Column(nullable = false)
	private Boolean isCanceled;
	private String title;
	@Column(nullable = false)
	private String channelId;
	@Column(nullable=false)
	private String creatorId;
	private LocalDateTime scheduledTime;
	private LocalDateTime endDate;
	private Boolean isActive;
	@Column
	@Convert(converter=IntegerSetConverter.class)
	private Set<Integer> scheduledDaysOfWeek;
	
	@Column(columnDefinition = "TEXT")
	@Convert(converter=SetStringConverter.class)
	private Set<String> emailsReceivedNotification;
	
	@Column(columnDefinition = "TEXT")
	@Convert(converter=ReactionConverter.class)
	private List<MessageReaction> reactions;
}
