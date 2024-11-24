package com.HungTran.MeetingTeam.Service;

import java.time.LocalDateTime;
import java.util.*;

import com.HungTran.MeetingTeam.DTO.LoginDTO;
import com.HungTran.MeetingTeam.Config.JwtConfig;
import com.HungTran.MeetingTeam.Util.CookieUtils;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.HungTran.MeetingTeam.Converter.UserConverter;
import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.RoleRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Security.CustomUserDetails;
import com.HungTran.MeetingTeam.Security.JwtProvider;
import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.InfoChecking;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepo userRepo;
	private final PasswordEncoder encoder;
	private final JwtProvider jwtProvider;
	private final RoleRepo roleRepo;
	private final InfoChecking infoChecking;
	private final UserConverter userConverter;
	private final AuthenticationManager authManager;
	private final MailService mailService;
	private final JwtConfig jwtConfig;
	private final CookieUtils cookieUtils;
	private Random random=new Random();

	@Transactional
	public void addUser(UserDTO dto) {
		User u=userConverter.convertDTOToUser(dto);
		if(u.getEmail()==null||!infoChecking.checkEmail(u.getEmail())) throw new RequestException("Email is invalid");
		if(userRepo.existsByEmail(u.getEmail())) throw new RequestException("Email "+u.getEmail()+" has already existed");
		if(u.getPassword()==null||u.getPassword().trim()=="") 
			throw new RequestException("Password is invalid");
		u.setRole(roleRepo.findByRoleName(Constraint.USER));
		u.setPassword(encoder.encode(u.getPassword()));
		u.setLastActive(LocalDateTime.now());
		u.setProvider(Constraint.CUSTOM);
		u.setIsActivated(false);
		sendOTPcode(u);
	}

	public void activateUser(String email, String OTPcode) {
		User u=userRepo.findByEmail(email).orElseThrow(()->new RequestException("Email "+email+" does not exists"));
		if(u.getOTPtime()==null||u.getOTPtime().isBefore(LocalDateTime.now()))
			throw new RequestException("The otp code has been expired! Please enter new OTP");
		if(u.getOTPcode()==null||!u.getOTPcode().equals(OTPcode))
			throw new RequestException("OTP code "+OTPcode+" is wrong");
		u.setOTPcode(null);
		u.setOTPtime(null);
		u.setIsActivated(true);
		userRepo.save(u);
	}

	public Map.Entry<Cookie,LoginDTO> login(String email, String password) {
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(email,password));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token= jwtProvider.generateToken(authentication);
		var authCookie=cookieUtils.generateTokenCookie(token);

		CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
		var userDTO= userConverter.convertUserToDTO(userDetails.getU());
		var expiredDate=new Date((new Date()).getTime() + jwtConfig.expiration);
		var loginDTO=new LoginDTO(userDTO, expiredDate);

		return new AbstractMap.SimpleImmutableEntry<>(authCookie,loginDTO);
	}

	public void sendOTPcode(String email) {
		User u=userRepo.findByEmail(email).orElseThrow(()->new RequestException("Email "+email+" does not exists"));
		sendOTPcode(u);
	}

	public void sendOTPcode(User u) {
		String otp="";
		for(int i=0;i<6;i++) otp+=random.nextInt(9);
		u.setOTPcode(otp);
		u.setOTPtime(LocalDateTime.now().plusMinutes(5));
		mailService.sendOTPMail(u.getEmail(),otp);
		userRepo.save(u);
	}

	public void changePassword(String email, String newPassword, String OTPcode) {
		User u=userRepo.findByEmail(email).orElseThrow(()->new RequestException("Email "+email+" does not exists"));
		if(u.getOTPtime()==null||u.getOTPtime().isBefore(LocalDateTime.now()))
			throw new RequestException("The otp code has been expired! Please enter new OTP");
		if(u.getOTPcode()==null||!u.getOTPcode().equals(OTPcode))
			throw new RequestException("OTP code "+OTPcode+" is wrong");
		u.setPassword(encoder.encode(newPassword));
		u.setOTPcode(null);
		u.setOTPtime(null);
		userRepo.save(u);
	}

	public void checkAndUpdatePassword(String currentPassword,String newPassword, User u) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(u.getEmail(),currentPassword));
			u.setPassword(encoder.encode(newPassword));
		} catch (Exception e) {
			throw new RequestException("Current password is invalid");
		}
	}
}
