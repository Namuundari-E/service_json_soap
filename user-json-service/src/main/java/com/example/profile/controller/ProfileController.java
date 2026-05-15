package com.example.profile.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.profile.model.Profile;
import com.example.profile.repository.ProfileRepository;
import com.example.profile.service.FileService;
import java.util.*;

@RestController
@RequestMapping("/users")
@CrossOrigin // Frontend-өөс хандах боломж олгоно
public class ProfileController {
    
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private FileService fileService;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Collections.singletonMap("status", "ok");
    }

    @PostMapping
    public Profile save(@RequestBody Profile profile) {
        return profileRepository.save(profile);
    }

    @GetMapping("/me")
    public Profile get() {
        return getOrCreateCurrentProfile();
    }

    @PutMapping("/me")
    public Profile updateMe(@RequestBody Profile profileDetails) {
        Profile profile = getOrCreateCurrentProfile();
        profile.setName(profileDetails.getName());
        profile.setBio(profileDetails.getBio());
        profile.setPhone(profileDetails.getPhone());
        if (profileDetails.getImageUrl() != null) {
            profile.setImageUrl(profileDetails.getImageUrl());
        }
        return profileRepository.save(profile);
    }

    @PutMapping("/{id}")
    public Profile update(@PathVariable Long id, @RequestBody Profile profileDetails) {
        return profileRepository.findById(id).map(profile -> {
            profile.setName(profileDetails.getName());
            profile.setBio(profileDetails.getBio());
            profile.setPhone(profileDetails.getPhone());
            if (profileDetails.getImageUrl() != null) {
                profile.setImageUrl(profileDetails.getImageUrl());
            }
            return profileRepository.save(profile);
        }).orElseThrow(() -> new RuntimeException("Profile not found!"));
    }

    // Delete Profile
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        profileRepository.deleteById(id);
        return "Profile deleted!";
    }

    // New Endpoint for uploading profile image
    @PostMapping("/{id}/image")
    public Profile uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadFile(file);
        
        return profileRepository.findById(id).map(profile -> {
            profile.setImageUrl(fileUrl);
            return profileRepository.save(profile);
        }).orElseThrow(() -> new RuntimeException("Profile not found!"));
    }

    // Fallback if uploading to 'me'
    @PostMapping("/me/image")
    public Profile uploadImageToMe(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadFile(file);

        Profile user = getOrCreateCurrentProfile();
        user.setImageUrl(fileUrl);
        return profileRepository.save(user);
    }

    private Profile getOrCreateCurrentProfile() {
        return profileRepository.findById(1L)
                .or(() -> profileRepository.findAll().stream().findFirst())
                .orElseGet(() -> profileRepository.save(new Profile("Default", "No bio", "000")));
    }
}
