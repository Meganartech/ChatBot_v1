package com.example.LiveChat.DTO;

import com.example.LiveChat.Model.Questions;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionResponseDTO {
    private Long id;
    private String question;
    private String answer;
    private String keywords;
    private String createdBy; // Store as email
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public QuestionResponseDTO(Questions question) {
        this.id = question.getId();
        this.question = question.getQuestion();
        this.answer = question.getAnswer();
        this.keywords = question.getKeywords();
        this.createdBy = question.getCreatedBy();
    }
}
