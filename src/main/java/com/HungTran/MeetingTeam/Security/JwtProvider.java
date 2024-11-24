package com.HungTran.MeetingTeam.Security;

import java.util.Date;

import com.HungTran.MeetingTeam.Config.JwtConfig;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtProvider {
	@Autowired
	JwtConfig jwtConfig;

	public String generateToken(Authentication auth) {
		String id=auth.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtConfig.expiration);
		String role = "";
		for (GrantedAuthority authority : auth.getAuthorities()) {
		    role = authority.getAuthority();
		}
		String token= Jwts.builder()
				.setSubject(id)
				.setIssuedAt(currentDate)
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512,jwtConfig.secret.getBytes())
				.compact();
		return jwtConfig.prefix+token;
	}

	public String getIdFromToken(String token) {
		try {
			String content=token.substring(jwtConfig.prefix.length(), token.length());
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(jwtConfig.secret.getBytes())
					.build()
					.parseClaimsJws(content)
					.getBody();
			return claims.getSubject();
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException("JWT was exprired or incorrect");
		}
	}
}
