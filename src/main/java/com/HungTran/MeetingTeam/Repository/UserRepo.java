package com.HungTran.MeetingTeam.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HungTran.MeetingTeam.Model.FriendRelation;
import com.HungTran.MeetingTeam.Model.User;

import jakarta.transaction.Transactional;
@Repository
public interface UserRepo extends JpaRepository<User,String>{
	public boolean existsByEmail(String email);
	
	public Optional<User> findByEmail(String email);
	@Query("select fr1.friend2 from FriendRelation fr1 where fr1.friend1.id=?1 and fr1.status='FRIEND'"
			+"union select fr2.friend1 from FriendRelation fr2 where fr2.friend2.id=?1 and fr2.status='FRIEND'")
	public List<User> getFriends(String userId);
	
	@Query("select fr1.friend2.id from FriendRelation fr1 where fr1.friend1.id=?1 and fr1.status='FRIEND'"
			+"union select fr2.friend1.id from FriendRelation fr2 where fr2.friend2.id=?1 and fr2.status='FRIEND'")
	public List<String> getFriendIds(String userId);
	
	@Query("select count(fr) from FriendRelation fr where ((fr.friend1.id=?1 and fr.friend2.id=?2) or (fr.friend1.id=?2 and fr.friend2.id=?1)) and fr.status='FRIEND'")
	public int havingFriend(String userId, String friendId);
	
	@Modifying
	@Transactional
	@Query("update User set status=?1, lastActive=?2 where id=?3")
	public void updateStatusAndLastActive(String status,LocalDateTime lastActive,String id);
}
