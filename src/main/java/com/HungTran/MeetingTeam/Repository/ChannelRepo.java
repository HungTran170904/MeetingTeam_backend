package com.HungTran.MeetingTeam.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.HungTran.MeetingTeam.Model.Channel;
import com.HungTran.MeetingTeam.Model.Message;
import com.HungTran.MeetingTeam.Model.Team;

public interface ChannelRepo extends JpaRepository<Channel, String>{
	@Query("select channel.team.id from Channel channel where channel.id=?1")
	public String findTeamIdById(String channelId);
	
	@Query("select channel.team from Channel channel where channel.id=?1")
	public Team findTeamById(String channelId);
	
	@Query("select channel.team.teamName from Channel channel where channel.id=?1")
	public String findTeamNameById(String channelId);

	@Query("select m.id from Meeting m where m.channelId=?1")
	public List<String> getMeetingIdsById(String channelId);;
}
