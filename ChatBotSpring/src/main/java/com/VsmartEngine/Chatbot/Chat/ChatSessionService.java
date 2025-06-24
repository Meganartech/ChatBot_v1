package com.VsmartEngine.Chatbot.Chat;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatSessionService {
	
	 @Autowired
	 private ChatSessionRepository chatSessionRepository;

	 public ChatSession getOrCreateSession(Long senderId, Long receiverId) {
	        Optional<ChatSession> optionalSession = 
	            chatSessionRepository.findLatestSessionBetweenUsers(senderId, receiverId);

	        if (optionalSession.isPresent()) {
	            ChatSession existingSession = optionalSession.get();
	            if (existingSession.isStatus()) {
	                return existingSession;
	            } else {
	                existingSession.setStatus(false); // Set old session as offline
	                chatSessionRepository.save(existingSession);
	            }
	        }

	        ChatSession newSession = new ChatSession();
	        newSession.setSenderid(senderId);
	        newSession.setReceiverid(receiverId);
	        newSession.setUserId(senderId);
	        newSession.setAdminId(receiverId);
	        newSession.setStatus(true);

	        return chatSessionRepository.save(newSession);
	    }

	    public void markSessionsOfflineForUser(Long userId) {
	        List<ChatSession> sessions = chatSessionRepository.findByUserIdAndStatusTrue(userId);
	        for (ChatSession session : sessions) {
	            session.setStatus(false);
	            chatSessionRepository.save(session);
	        }
	    }

}
