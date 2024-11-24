package com.HungTran.MeetingTeam.Controller;

import com.HungTran.MeetingTeam.DTO.LoginDTO;
import com.HungTran.MeetingTeam.Util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.HungTran.MeetingTeam.DTO.UserDTO;
import com.HungTran.MeetingTeam.Service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final CookieUtils cookieUtils;

	@PostMapping("/registerUser")
	public ResponseEntity<HttpStatus> registerUser(
			@RequestBody UserDTO dto) throws Exception{
		authService.addUser(dto);
		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/login")
	public ResponseEntity<LoginDTO> login(
			@RequestParam("email") String username,
			@RequestParam("password") String password,
			HttpServletResponse response){
		var pair=authService.login(username, password);
		response.addCookie(pair.getKey());
		return ResponseEntity.ok(pair.getValue());
	}
	@PostMapping("/changePassword")
	public ResponseEntity<HttpStatus> changePassword(
			@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("OTPcode") String OTPcode
			){
		authService.changePassword(email, newPassword, OTPcode);
		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/activateUser")
	public ResponseEntity<HttpStatus> activateUser(
			@RequestParam("email") String email,
			@RequestParam("OTPcode") String OTPcode){
		authService.activateUser(email, OTPcode);
		return new ResponseEntity(HttpStatus.OK);
	}
	@GetMapping("/sendOTPcode")
	public ResponseEntity<HttpStatus> sendOTPcode(
			@RequestParam("email") String email){
		authService.sendOTPcode(email);
		return new ResponseEntity(HttpStatus.OK);
	}
	@GetMapping("/logout")
	public ResponseEntity<HttpStatus> logout(
			HttpServletResponse response){
		Cookie expiredCookie = cookieUtils.generateExpiredCookie();
		response.addCookie(expiredCookie);
		return new ResponseEntity(HttpStatus.OK);
	}
}
