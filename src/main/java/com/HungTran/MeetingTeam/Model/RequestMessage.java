package com.HungTran.MeetingTeam.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestMessage {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name="senderId")
	private User sender; 
	@ManyToOne
	@JoinColumn(name="recipientId")
	private User recipient; // friend request
	@ManyToOne
	@JoinColumn(name="teamId")
	private Team team; // team request
	@Column(columnDefinition = "TEXT")
	private String content;
	private LocalDateTime createdAt;
}
