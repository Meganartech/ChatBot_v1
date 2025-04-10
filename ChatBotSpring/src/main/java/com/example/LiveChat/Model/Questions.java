package com.example.LiveChat.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String question;

    @Column(nullable = false, length = 500)
    private String answer;

    @Column(nullable = false)
    private String keywords;

    @Column(nullable = false) // Stores the admin's email who created this question
    private String createdBy;

    // ✅ Many-to-One relationship with Property
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
}
