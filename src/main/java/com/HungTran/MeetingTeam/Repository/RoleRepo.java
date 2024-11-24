package com.HungTran.MeetingTeam.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HungTran.MeetingTeam.Model.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Integer>{
	public Role findByRoleName(String roleName);
}
