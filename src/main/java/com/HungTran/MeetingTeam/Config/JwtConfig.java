package com.HungTran.MeetingTeam.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class JwtConfig {
	@Value("${security.jwt.header}")
	public String header;

	@Value("${security.jwt.prefix}")
	public String prefix;

	@Value("${security.jwt.secret}")
	public String secret;

	@Value("${security.jwt.expiration}")
	public int expiration;

	@Value("${security.jwt.cookie-domain}")
	public String cookieDomain;
}
