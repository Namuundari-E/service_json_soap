package com.example.profile.controller;

import org.springframework.web.bind.annotation.*;
import lombok.*;
import java.util.*;

@RestController
@RequestMapping("/users")
@CrossOrigin // Frontend-өөс хандах боломж олгоно
public class ProfileController {
    
    private Map<String, Profile> db = new HashMap<>();

    @PostMapping
    public String save(@RequestBody Profile profile) {
        db.put("current", profile);
        return "Profile updated!";
    }

    @GetMapping("/me")
    public Profile get() {
        return db.getOrDefault("current", new Profile("Default", "No bio", "000"));
    }
 // Update Profile
    @PutMapping("/{id}")
    public String update(@PathVariable String id, @RequestBody Profile profile) {
        if(db.containsKey(id)) {
            db.put(id, profile);
            return "Profile updated successfully!";
        }
        return "Profile not found!";
    }

    // Delete Profile
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        db.remove(id);
        return "Profile deleted!";
    }
}

@Data @AllArgsConstructor @NoArgsConstructor
class Profile {
    private String name;
    private String bio;
    private String phone;
}