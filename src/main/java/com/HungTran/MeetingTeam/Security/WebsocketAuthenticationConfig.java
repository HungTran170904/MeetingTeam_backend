package com.HungTran.MeetingTeam.Security;

import java.util.Map;

import com.HungTran.MeetingTeam.Config.JwtConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebsocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {
	private static Logger LOGGER=LoggerFactory.getLogger(WebsocketAuthenticationConfig.class);
	@Autowired
	JwtProvider jwtProvider;
	@Autowired
    JwtConfig jwtConfig;
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor=
						MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if(StompCommand.CONNECT.equals(accessor.getCommand())) {
					System.out.println("Init a websocket connection");
					Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
					String token=sessionAttributes.get(jwtConfig.header).toString();
					LOGGER.debug("Authorization {}", token);

					String userId=jwtProvider.getIdFromToken(token);
					CustomUserDetails userDetails=customUserDetailsService.loadUserById(userId);
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
			                   userDetails.getAuthorities());
					accessor.setUser(authenticationToken);
				}
				return message;
			}
		});
	}
}
