package com.HungTran.MeetingTeam.Repository;

import com.HungTran.MeetingTeam.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.HungTran.MeetingTeam.Model.FriendRelation;

import jakarta.transaction.Transactional;

@Repository
public interface FriendRelationRepo extends JpaRepository<FriendRelation,String> {
    @Query("select fr from FriendRelation fr where (fr.friend1=?1 and fr.friend2=?2) or (fr.friend1=?2 and fr.friend2=?1)")
	public FriendRelation findByUsers(User user1,User user2);

    @Query("update FriendRelation set status=?1 where (friend1.id=?2 and friend2.id=?3) " +
            "or (friend1.id=?3 and friend2.id=?2)")
    @Modifying
    @Transactional
    public int updateFriendStatus(String status, String friendId1, String friendId2);
}
