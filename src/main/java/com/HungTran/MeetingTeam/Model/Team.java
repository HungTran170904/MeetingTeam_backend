package com.HungTran.MeetingTeam.Model;

import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Team {
	@Id
	@UuidGenerator
	private String id;
	private String teamName;
	private String urlIcon;
	private Boolean autoAddMember;
	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	private List<TeamMember> members;
	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	private List<Channel> channels;
	public Team(String id) {
		this.id=id;
	}
}
