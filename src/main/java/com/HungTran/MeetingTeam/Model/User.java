package com.HungTran.MeetingTeam.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base32;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import com.HungTran.MeetingTeam.Converter.SetStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id @UuidGenerator
	private String id;
	@Column(nullable=false, unique=true)
	private String email;
	private String nickName;
	private String password;
	private String urlIcon;
	private LocalDate birthday;
	private String phoneNumber;
	private String OTPcode; // for change password
	private LocalDateTime OTPtime;
	private Boolean isActivated;
	private LocalDateTime lastActive;
	private String status; //ONLINE, OFFLINE
	private String provider;
	@ManyToOne
	@JoinColumn(name="roleId")
	private Role role;
	@Column(columnDefinition = "TEXT")
	@Convert(converter=SetStringConverter.class)
	private Set<String> calendarMeetingIds;
	public User(String id) {
		this.id=id;
	}
}
