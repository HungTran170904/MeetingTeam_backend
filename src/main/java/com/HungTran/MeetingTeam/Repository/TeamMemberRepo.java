package com.HungTran.MeetingTeam.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.TeamMember;
import com.HungTran.MeetingTeam.Model.User;

import jakarta.transaction.Transactional;

public interface TeamMemberRepo extends JpaRepository<TeamMember,String> {
	public boolean existsByTeamAndU(Team team, User u);
	
	@Query("select count(tm) from TeamMember tm where tm.u=?1 and "
			+ "tm.team.id in (select c.team.id from Channel c where c.id=?2)")
	public int existsByChannelIdAndU(User u, String channelId);
	
	@Query("select tm from TeamMember tm where tm.team.id=?1 and tm.u.id=?2")
	public TeamMember findByTeamIdAndUserId(String teamId, String userId);

	@Query("select tm.u from TeamMember tm where tm.team.id=?1")
	public List<User> findUsersByTeamId(String teamId);
	
	@Query("select tm.role from TeamMember tm where tm.u.id=?1 and tm.team.id=?2")
	public String getRoleByUserIdAndTeamId(String userId, String teamId);
}
