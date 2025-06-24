package com.VsmartEngine.Chatbot.UserInfo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.VsmartEngine.Chatbot.Chat.ChatSessionService;

@Component
public class UserStatusScheduler {
	
	  @Autowired
	  private UserInfoRepository userInfoRepository;

	  @Autowired
	  private ChatSessionService chatSessionService;

	  @Scheduled(fixedRate = 60000)
	    public void checkAndSetOfflineUsers() {
	        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);
	        List<UserInfo> users = userInfoRepository.findAll();

	        for (UserInfo user : users) {
	            if (user.getLastSeen() == null || user.getLastSeen().isBefore(threshold)) {
	                chatSessionService.markSessionsOfflineForUser(user.getId());
	            }
	        }
	    }

}
