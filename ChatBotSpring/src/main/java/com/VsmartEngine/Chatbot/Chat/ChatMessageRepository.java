package com.VsmartEngine.Chatbot.Chat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{

	List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId);
	
	ChatMessage findTopBySessionIdOrderByTimestampDesc(String sessionId);
	
	 long countBySessionId(String sessionId);

	 long countBySessionIdAndRole(String sessionId, String role);

     long countBySessionIdAndRoleAndTimestampAfter(String sessionId, String role, LocalDateTime timestamp);

     @Query(value = "SELECT m.session_id AS session_id, COUNT(*) AS unread_count "
                   + "FROM chat_message m "
                   + "JOIN chat_session s ON s.session_id = m.session_id "
                   + "WHERE s.receiver = :receiverEmail "
                   + "AND m.role = 'USER' "
                   + "AND (s.last_admin_read_at IS NULL OR m.timestamp > s.last_admin_read_at) "
                   + "GROUP BY m.session_id", nativeQuery = true)
     List<Object[]> findUnreadCountsByReceiver(@Param("receiverEmail") String receiverEmail);

}
