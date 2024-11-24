package com.HungTran.MeetingTeam.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.HungTran.MeetingTeam.Repository.FriendRelationRepo;
import com.HungTran.MeetingTeam.Util.Constraint;
import com.HungTran.MeetingTeam.Util.SocketTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HungTran.MeetingTeam.Converter.UserConverter;
import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Exception.RequestException;
import com.HungTran.MeetingTeam.Exception.MessageException;
import com.HungTran.MeetingTeam.Model.FriendRelation;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.User;
import com.HungTran.MeetingTeam.Repository.RequestMessageRepo;
import com.HungTran.MeetingTeam.Repository.RoleRepo;
import com.HungTran.MeetingTeam.Repository.UserRepo;
import com.HungTran.MeetingTeam.Util.InfoChecking;
import com.cloudinary.Cloudinary;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepo userRepo;
	private final FriendRelationRepo frRepo;
	private final UserConverter userConverter;
	private final InfoChecking infoChecking;
	private final AuthService authService;
	private final CloudinaryService cloudinaryService;
	private final SocketTemplate socketTemplate;

	public UserDTO getUserInfo() {
		return userConverter.convertUserToDTO(infoChecking.getUserFromContext());
	}
	public UserDTO updateUser(UserDTO dto,String currentPassword,MultipartFile file) {
		User user=infoChecking.getUserFromContext();
		user.setBirthday(dto.getBirthday());
		user.setNickName(dto.getNickName());
		user.setPhoneNumber(dto.getPhoneNumber());
		if(user.getProvider().equals(Constraint.CUSTOM)&&
				currentPassword!=null&&
				!currentPassword.equals("")) {
			authService.checkAndUpdatePassword(currentPassword,dto.getPassword(), user);
		}
		if(file!=null) {
			String url=cloudinaryService.uploadFile(file,user.getId(),user.getUrlIcon());
			user.setUrlIcon(url);
		}
		UserDTO savedDTO=userConverter.convertUserToDTO(userRepo.save(user));
		List<String> friendIds=userRepo.getFriendIds(user.getId());
		for(String friendId: friendIds) {
			socketTemplate.sendUser(friendId,"/updateFriends",savedDTO);
		}
		return savedDTO;
	}
	public List<UserDTO> getFriends(){
		String userId=infoChecking.getUserIdFromContext();
		return userConverter.convertUserToDTO(userRepo.getFriends(userId));
	}
	@Transactional
	public void unfriend(String friendId) {
		String userId=infoChecking.getUserIdFromContext();
		frRepo.updateFriendStatus("UNFRIEND",userId, friendId);
		socketTemplate.sendUser(friendId,"/deleteFriend",userId);
		socketTemplate.sendUser(userId,"/deleteFriend", friendId);
	}
}
