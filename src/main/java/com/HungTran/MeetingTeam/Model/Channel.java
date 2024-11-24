package com.HungTran.MeetingTeam.Model;

import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {
	@Id
	@UuidGenerator
	private String id;
	private String channelName;
	@Column(columnDefinition = "TEXT")
	private String description;
	private String type; // ChatChannel, VideoCallChannel
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="teamId")
	private Team team;
}
