package com.example.stripify.controller;

import com.example.stripify.model.Song;
import com.example.stripify.model.User;
import com.example.stripify.service.UserService;
import com.example.stripify.util.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity signup(@RequestBody User user) {
        return ResponseEntity.ok(userService.signup(user));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
        return ResponseEntity.ok(new JwtResponse(userService.login(user), user.getUsername()));
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
