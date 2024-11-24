package com.HungTran.MeetingTeam.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HungTran.MeetingTeam.Model.RequestMessage;
import com.HungTran.MeetingTeam.Model.Team;
import com.HungTran.MeetingTeam.Model.User;
@Repository
public interface RequestMessageRepo extends JpaRepository<RequestMessage,Integer> {
	public Boolean existsBySenderAndTeam(User sender,Team team);
	
	@Query("select m from RequestMessage m where m.recipient.id=?1 or (m.sender.id=?1 and m.recipient is not null)")
	public List<RequestMessage> getFriendRequests(String userId);
	
	@Query("select m from RequestMessage m where m.team.id=?1")
	public List<RequestMessage> getTeamRequestMessages(String teamId);
	
	@Query("select m from RequestMessage m where m.sender.id=?1 and m.team is not null")
	public List<RequestMessage> getSendedRequestMessages(String senderId);
}
