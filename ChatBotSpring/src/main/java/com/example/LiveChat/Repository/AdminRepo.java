package com.example.LiveChat.Repository;

import com.example.LiveChat.Model.Admin;
import com.example.LiveChat.Model.ChatbotType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepo extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    // ✅ Find an admin by email and chatbot type (useful for filtering)
    Optional<Admin> findByEmailAndChatbotType(String email, ChatbotType chatbotType);
}
