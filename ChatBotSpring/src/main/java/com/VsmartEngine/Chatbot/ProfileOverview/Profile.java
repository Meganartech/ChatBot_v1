package com.VsmartEngine.Chatbot.ProfileOverview;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private String url;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    public Profile(Long id, String name, boolean status, String url, byte[] imageData) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.url = url;
        this.imageData = imageData;
    }
    
}