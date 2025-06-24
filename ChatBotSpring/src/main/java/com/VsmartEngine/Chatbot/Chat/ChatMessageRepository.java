package com.VsmartEngine.Chatbot.Chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{
	
	List<ChatMessage> findBySessionIdOrderByTimestampAsc(Long sessionId);
	
	ChatMessage findTopBySessionIdOrderByTimestampDesc(Long sessionId);

	
	   List<ChatMessage> findBySenderAndReceiver(Long sender, Long receiver);
	    
	   List<ChatMessage> findBySessionId(Long sessionId);
	    
	   List<ChatMessage> findBySenderOrReceiver(Long sender, Long receiver);
	   
	   List<ChatMessage> findChatBySessionIdAndSenderAndReceiver(Long sessionId, Long sender, Long receiver);
	    
	   List<ChatMessage> findBySessionIdAndSender(Long sessionId, Long sender);
	   
}
