package com.HungTran.MeetingTeam.Security;


import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.HungTran.MeetingTeam.Util.Constraint;

import jakarta.servlet.http.HttpServletResponse;
//https://stackoverflow.com/questions/54237851/cors-problems-with-spring-security-and-websocket
@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig {
	@Value("${frontend.url}")
	private String frontendUrl;
	@Autowired
	private OAuth2LoginSuccessHandler successHandler;
	@Autowired
	private CustomStatelessAuthorizationRequestRepository authorizationRequestRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration authConfig) throws Exception{
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public ZegoTokenFilter zegoTokenFilter() {
		return new ZegoTokenFilter();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:3000", frontendUrl));
		config.setAllowedMethods(Arrays.asList("*"));
		config.setAllowCredentials(true);
		config.setAllowedHeaders(List.of("*"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // '/**' means apply this cors configuration to all endpoints
        return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling().authenticationEntryPoint(new AuthEntryPoint())
			.and()
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(zegoTokenFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests((requests) -> requests
					.requestMatchers("/wss/**", "/**/auth/**").permitAll()
					.requestMatchers("/**/admin/**").hasRole(Constraint.ADMIN)
					.anyRequest().authenticated())
			.oauth2Login(oath2->{
				oath2.authorizationEndpoint(subconfig->{
					subconfig.authorizationRequestRepository(authorizationRequestRepository);
				});
				oath2.loginPage("/login").successHandler(successHandler).permitAll();
			});
		return http.build();
	}
}
