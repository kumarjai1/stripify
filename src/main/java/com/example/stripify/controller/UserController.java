package com.example.stripify.controller;

import com.example.stripify.model.Song;
import com.example.stripify.model.User;
import com.example.stripify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/user/list")
    public Iterable<User> listUsers() {
        return userService.listUsers();
    }

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signup(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @DeleteMapping("/user/{userId}")
    public Long deleteUserById(@PathVariable Long userId) {
        return userService.deleteById(userId);
    }

    @PostMapping("/{username}/{songId}")
    public List<Song> addSong(@PathVariable String username, @PathVariable Long songId) throws Exception {
        return userService.addSong(username, songId);
    }
}
