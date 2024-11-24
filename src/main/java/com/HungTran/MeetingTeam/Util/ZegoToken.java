package com.HungTran.MeetingTeam.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZegoToken {
	@Value("${zegocloud.app-id}")
	private long appId;
	@Value("${zegocloud.secret-server}")
	private String secretServer;
	
	public String generateToken(String userId, String roomId) {
		String payload = String.format("{\"room_id\":\"%s\"}", roomId);
		TokenServerAssistant.TokenInfo token=
				TokenServerAssistant.generateToken04(appId,userId,secretServer,3600*24,payload);
		return token.data;
	}
}
