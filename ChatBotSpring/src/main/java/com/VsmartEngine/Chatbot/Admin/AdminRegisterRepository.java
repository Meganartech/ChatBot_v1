package com.VsmartEngine.Chatbot.Admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface AdminRegisterRepository extends JpaRepository<AdminRegister,Long>{
	
	Optional<AdminRegister> findByEmail(String email);
	
	Optional<AdminRegister> findByCode(String code);
	
	@Query("SELECT new com.VsmartEngine.Chatbot.Admin.AdminUserDto(a.id, a.username) FROM AdminRegister a WHERE a.status = true")
	List<AdminUserDto> findAllUserIdAndUsername();

	
}
