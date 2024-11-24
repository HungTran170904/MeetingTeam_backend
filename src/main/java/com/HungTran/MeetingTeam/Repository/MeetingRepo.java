package com.HungTran.MeetingTeam.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.HungTran.MeetingTeam.Model.Meeting;

import jakarta.transaction.Transactional;
@Repository
public interface MeetingRepo extends JpaRepository<Meeting,String>{
	@Query("select m from Meeting m where m.channelId=?1 order by m.createdAt DESC")
	public List<Meeting> getMeetingsByChannelId(String channelId, Pageable pageable);

	@Query("select m from Meeting m where m.scheduledTime IS NOT NULL and m.isCanceled=false and m.id in :meetingIds")
	public List<Meeting> getByIds(@Param("meetingIds") Set<String> meetingIds);
}
