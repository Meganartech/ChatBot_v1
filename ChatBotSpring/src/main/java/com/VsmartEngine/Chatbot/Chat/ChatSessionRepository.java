package com.VsmartEngine.Chatbot.Chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession,Long>{
	
    Optional<ChatSession> findBySenderidAndReceiverid(Long senderId, Long receiverId);
    
    @Query("SELECT s FROM ChatSession s WHERE ((s.senderid = :senderId AND s.receiverid = :receiverId) OR (s.senderid = :receiverId AND s.receiverid = :senderId)) AND s.status = true")
    Optional<ChatSession> findActiveSessionBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    
    @Query("SELECT s FROM ChatSession s WHERE " +
            "((s.senderid = :senderId AND s.receiverid = :receiverId) OR " +
            "(s.senderid = :receiverId AND s.receiverid = :senderId)) " +
            "ORDER BY s.sessionid DESC")
     Optional<ChatSession> findLatestSessionBetweenUsers(@Param("senderId") Long senderId,
                                                         @Param("receiverId") Long receiverId);
    
    List<ChatSession> findByUserIdAndStatusTrue(Long userId);

    boolean existsByUserIdAndStatusTrue(Long userId);
    
//    @Query("SELECT s FROM ChatSession s WHERE " +
//            "((s.senderid = :senderId AND s.receiverid = :receiverId) OR " +
//            "(s.senderid = :receiverId AND s.receiverid = :senderId)) " +
//            "ORDER BY s.sessionid DESC")
//     Optional<ChatSession> findLatestSessionBetweenUsers(@Param("senderId") Long senderId,
//                                                         @Param("receiverId") Long receiverId);
//
//     List<ChatSession> findByUserIdAndStatusTrue(Long userId);
//
//     boolean existsByUserIdAndStatusTrue(Long userId);

}
