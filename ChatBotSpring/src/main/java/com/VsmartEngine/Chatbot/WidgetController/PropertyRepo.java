package com.VsmartEngine.Chatbot.WidgetController;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepo extends JpaRepository<Property, Long> {

    // ✅ Find all properties for a specific admin
    List<Property> findAllByAdminEmail(String adminEmail);

    // ✅ Find Property by Unique Property ID
    Optional<Property> findByUniquePropertyId(String uniquePropertyId);

    // ✅ Check if Property exists by Website URL
    boolean existsByWebsiteURL(String websiteURL);

    // ✅ Find Properties by Chatbot Type
    List<Property> findByChatbotType(ChatbotType chatbotType);
}
