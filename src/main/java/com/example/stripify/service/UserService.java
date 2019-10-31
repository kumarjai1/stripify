package com.example.stripify.service;

import com.example.stripify.model.Song;
import com.example.stripify.model.User;
import com.example.stripify.util.JwtResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashMap;
import java.util.List;

public interface UserService extends UserDetailsService {

    Iterable<User> listUsers();
    JwtResponse signup(User user);
    String login (User user);
    Long deleteById(Long userId);
    User getUser(String username);

    User updateUser(User user);

    List<Song> addSong(String username, Long songId) throws Exception;
}
