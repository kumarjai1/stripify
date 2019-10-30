package com.example.stripify.service;

import com.example.stripify.model.Song;
import com.example.stripify.model.User;

import java.util.List;

public interface UserService {

    Iterable<User> listUsers();
    User signup(User user);
    User login(String username, String password);
    Long deleteById(Long userId);
    User getUser(String username);

    User updateUser(User user);

    List<Song> addSong(String username, Long songId) throws Exception;
}
