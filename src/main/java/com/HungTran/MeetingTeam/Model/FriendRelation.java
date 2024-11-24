package com.HungTran.MeetingTeam.Model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class FriendRelation {
	@Id @UuidGenerator
	private String id;
	@ManyToOne
	@JoinColumn(name="friend1Id", nullable=false)
	private User friend1;
	@ManyToOne
	@JoinColumn(name="friend2Id", nullable=false)
	private User friend2;
	private String status; //FRIEND, UNFRIEND
	public FriendRelation(User friend1, User friend2, String status) {
		this.friend1 = friend1;
		this.friend2 = friend2;
		this.status = status;
	}
}
