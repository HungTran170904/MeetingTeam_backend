package com.HungTran.MeetingTeam.Model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TeamMember {
	@Id @UuidGenerator
	private String id;
	@ManyToOne
	private User u;
	@ManyToOne(fetch=FetchType.LAZY)
	private Team team;
	private String role; //LEADER,DEPUTY, MEMBER, LEAVE (has leaved team)
	public TeamMember(User u, Team team, String role) {
		this.u = u;
		this.team = team;
		this.role = role;
	}
}
