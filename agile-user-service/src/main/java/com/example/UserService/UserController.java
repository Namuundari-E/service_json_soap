package com.example.UserService;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
@RestController
public class UserController {
    // In-memory list acting as our temporary database
    private List<User> users = new ArrayList<>();

    public UserController() {
        // Initial Mock Data
        users.add(new User(1, "Bat-Erdene", "bat@example.com"));
        users.add(new User(2, "Sarnai", "sarnai@example.com"));
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }
    @PostMapping("/users")
    public User addUser(@RequestBody User newUser) {
        users.add(newUser);
        return newUser; 
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return new ResponseEntity<>(user, HttpStatus.OK); // Return 200 OK
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 Not Found
    }
}
