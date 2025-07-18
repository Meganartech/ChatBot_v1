package com.VsmartEngine.Chatbot.ProfileOverview;

import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
@Controller
@RequestMapping("/api/profiles")
@CrossOrigin()
public class ProfileController {
    private final ProfileRepository repository;

    public ProfileController(ProfileRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Profile> createProfile(
            @RequestParam("name") String name,
            @RequestParam("status") boolean status,
            @RequestParam("url") String url,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        // Delete all existing profiles to ensure only one profile exists
        repository.deleteAll();

        byte[] imageData = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                imageData = imageFile.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

        Profile profile = Profile.builder()
                .name(name)
                .status(status)
                .url(url)
                .imageData(imageData)
                .build();

        return ResponseEntity.ok(repository.save(profile));
    }

    @GetMapping
    public ResponseEntity<Profile> getProfile() {
        // Return the first profile (or null if none exists)
        return ResponseEntity.ok(repository.findAll().stream().findFirst().orElse(null));
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfileImage() {
        Profile profile = repository.findAll().stream().findFirst().orElse(null);
        if (profile != null && profile.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(profile.getImageData());
        }
        return ResponseEntity.noContent().build();
    }
}

