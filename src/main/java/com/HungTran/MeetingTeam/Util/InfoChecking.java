package com.HungTran.MeetingTeam.Util;

import java.util.regex.Pattern;

import com.HungTran.MeetingTeam.Model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.HungTran.MeetingTeam.Security.CustomUserDetails;
@Component
public class InfoChecking {
	public boolean checkEmail(String email) {
		return Pattern.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$",email);
	}
	public String getUserIdFromContext() {
		Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
		return userDetails.getU().getId();
	}
	public User getUserFromContext() {
		Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
		return userDetails.getU();
	}
	public Integer findBestPageSize(Integer receivedNumber) {
		int bestPageSize=20;
		int maxMessagesNum=0;
		int messagesNum=0;
		for(int i=20;i>=2;i--) {
			messagesNum=i*(receivedNumber/i+1)-receivedNumber;
			if(messagesNum>maxMessagesNum) {
				maxMessagesNum=messagesNum;
				bestPageSize=i;
			}
			if(maxMessagesNum>=i-1) break;
		}
		System.out.println("BestPageSize:"+bestPageSize);
		return bestPageSize;
	}
}
