package com.VsmartEngine.Chatbot.Admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
	
	Optional<Role> findByRole(String role);

}
