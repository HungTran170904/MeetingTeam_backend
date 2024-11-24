package com.HungTran.MeetingTeam.Repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.HungTran.MeetingTeam.Model.Team;

import jakarta.transaction.Transactional;

public interface TeamRepo extends JpaRepository<Team,String>{
	@Query("select tm.team.id from TeamMember tm where tm.u.id=?1 and tm.role!='LEAVE'")
	public List<String> getTeamIdsByUserId(String userId);
	
	@Query("select team from Team team "
			+ "left join fetch team.members "
			+ "where team.id in :teamIds")
	public List<Team> getTeamsWithMembers(@Param("teamIds") List<String> teamIds);
	
	@Query("select team from Team team "
			+ "left join fetch team.channels "
			+ "where team in :teamIds")
	public List<Team> getTeamsWithChannels(@Param("teamIds") List<String> teamIds);
	@Query("select team from Team team left join fetch team.channels where team.id=?1")
	public Team getTeamWithChannels(String teamId);
	@Query("select team from Team team left join fetch team.members where team.id=?1")
	public Team getTeamWithMembers(String teamId);
}
