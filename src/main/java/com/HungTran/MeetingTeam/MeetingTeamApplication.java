package com.HungTran.MeetingTeam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeetingTeamApplication {
	public static void main(String[] args) {
		SpringApplication.run(MeetingTeamApplication.class, args);
	}
}
/*
 * Subscription:
 * 	/queue/teamId/chat
 * /queue/teamId/updateTeam
 * /queue/teamId/updateChannels
 * /queue/teamId/updateMembers
 * /queue/teamId/removeChannel
 * /queue/teamId/updateMeetings
 * /queue/teamId/deleteMeeting
 * /queue/teamId/meetingMessage
 * 	/user/userId/messages
 * /user/userId/friendRequest
 * /user/userId/deleteFriend
 * /user/userId/addFriendRequest
 * /user/userId/deleteFriendRequest
 */
