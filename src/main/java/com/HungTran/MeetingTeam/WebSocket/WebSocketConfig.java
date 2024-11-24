package com.HungTran.MeetingTeam.WebSocket;

import com.HungTran.MeetingTeam.Config.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
	@Value("${spring.rabbitmq.host}")
	private String rabbitmqHost;
	@Value("${stomp.rabbitmq.port}")
	private int rabbitmqPort;
	@Value("${spring.rabbitmq.username}")
	private String rabbitmqUsername;
	@Value("${spring.rabbitmq.password}")
	private String rabbitmqPassword;
	private final JwtConfig jwtConfig;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/wss").setAllowedOriginPatterns("*").withSockJS()
				.setInterceptors(httpSessionHandshakeInterceptor());
	}

	@Bean
	public HandshakeInterceptor httpSessionHandshakeInterceptor() {
		return new HandshakeInterceptor() {
			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
				if (request instanceof ServletServerHttpRequest) {
					ServletServerHttpRequest servletServerRequest = (ServletServerHttpRequest) request;
					HttpServletRequest servletRequest = servletServerRequest.getServletRequest();
					Cookie token = WebUtils.getCookie(servletRequest, jwtConfig.header);
					if(token != null) attributes.put(jwtConfig.header, token.getValue());
					else return false;
				}
				return true;
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
		};
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/api/socket");
		registry.setUserDestinationPrefix("/user");
		registry.enableStompBrokerRelay("/queue","/topic")
				.setRelayHost(rabbitmqHost)
				.setRelayPort(rabbitmqPort)
				.setSystemLogin(rabbitmqUsername)
				.setSystemPasscode(rabbitmqPassword)
				.setClientLogin(rabbitmqUsername)
				.setClientPasscode(rabbitmqPassword);
	}
}
