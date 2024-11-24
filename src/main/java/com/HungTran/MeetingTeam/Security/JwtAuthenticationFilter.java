package com.HungTran.MeetingTeam.Security;

import java.io.IOException;

import com.HungTran.MeetingTeam.Config.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	@Autowired
	JwtProvider jwtProvider;
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	@Autowired
    JwtConfig jwtConfig;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token=null;
		if(request.getCookies()!=null)
		for(var cookie : request.getCookies()){
			if(cookie.getName().equals(jwtConfig.header)){
				token=cookie.getValue();
				break;
			}
		}
		System.out.println("Url is "+request.getRequestURL());
		if(token==null||!token.startsWith(jwtConfig.prefix)) {
			chain.doFilter(request, response);
			return;
		}

		String id=jwtProvider.getIdFromToken(token);
		CustomUserDetails userDetails=customUserDetailsService.loadUserById(id);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                   userDetails.getAuthorities());
         SecurityContextHolder.getContext().setAuthentication(authenticationToken);
         chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path=request.getRequestURI();
		if(path.startsWith("/api/auth")||path.startsWith("/api/zegocloud"))
			return true;
		return !path.startsWith("/api");
	}
}
