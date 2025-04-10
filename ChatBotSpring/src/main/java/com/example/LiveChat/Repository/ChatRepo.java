package com.example.LiveChat.Repository;

import com.example.LiveChat.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {
    
    List<Chat> findBySenderAndReceiver(String sender, String receiver);
    
    List<Chat> findByPropertyId(Long propertyId);
    
    List<Chat> findBySenderOrReceiver(String sender, String receiver);
   
    List<Chat> findChatByPropertyIdAndSenderAndReceiver(Long propertyId, String sender, String receiver);
    
    List<Chat> findByPropertyIdAndSender(Long propertyId, String sender);
}
