package com.VsmartEngine.Chatbot.Admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminRegisterRepository extends JpaRepository<AdminRegister,Long>{
	
	Optional<AdminRegister> findByEmail(String email);
}
