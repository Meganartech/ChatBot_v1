package com.VsmartEngine.Chatbot.Chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession,Long>{
	
	 Optional<ChatSession> findBySessionId(String sessionId);
	 
	 Optional<ChatSession> findBySenderAndReceiver(String sender, String receiver);
	 
	 @Query("SELECT s FROM ChatSession s WHERE s.sender = :sender AND s.receiver = :receiver ORDER BY s.createdTime DESC")
	 Optional<ChatSession> findLatestSession(@Param("sender") String sender, @Param("receiver") String receiver);
	 
	 List<ChatSession> findByReceiver(String receiver);

}
