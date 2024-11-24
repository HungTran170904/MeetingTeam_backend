package com.HungTran.MeetingTeam.Security;

import java.util.List;

import com.HungTran.MeetingTeam.Util.Constraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Exception.PermissionException;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.UserRepo;


@Service
public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
	private UserRepo userRepo;
	@Override
	public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User u=userRepo.findByEmail(email).orElseThrow(()->new RequestException("Email "+email+" does not exists"));
		if(!u.getIsActivated()) throw new PermissionException("Account is not activated");
		if(!u.getProvider().equals(Constraint.CUSTOM))
			throw new RequestException("This account must be logined with social provider "+u.getProvider());
		return new CustomUserDetails(u);
	}
	public CustomUserDetails loadUserById(String id) {
		User u=userRepo.findById(id).orElseThrow(()->new RequestException("UserId "+id+" does not exists"));
		return new CustomUserDetails(u);
	}
}
