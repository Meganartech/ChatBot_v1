package com.example.LiveChat.Repository;

import com.example.LiveChat.Model.Property;
import com.example.LiveChat.Model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface QuestionRepo extends JpaRepository<Questions, Long> {

    // ✅ Find all questions for a specific property
    List<Questions> findByProperty(Property property);

    // ✅ Find all questions by property ID
    @Query("SELECT q FROM Questions q WHERE q.property.id = :propertyId")
    List<Questions> findAllByPropertyId(@Param("propertyId") Long propertyId);
    
    List<Questions> findByKeywordsContainingAndPropertyId(String keyword, Long propertyId);

	Optional<Questions> findByPropertyIdAndQuestionIgnoreCase(Long propertyId, String questionText);
}