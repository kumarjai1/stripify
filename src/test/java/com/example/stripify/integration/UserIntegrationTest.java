package com.example.stripify.integration;


import com.example.stripify.model.Song;
import com.example.stripify.model.User;
import com.example.stripify.model.UserRole;
import com.example.stripify.repository.SongRepository;
import com.example.stripify.repository.UserRepository;
import com.example.stripify.repository.UserRoleRepository;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("qa")
@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {RepoConfig.class, JwtUtil.class,
//        UserServiceImpl.class,
//        SongServiceImpl.class,
//        UserRoleServiceImpl.class})
@SpringBootTest
public class UserIntegrationTest {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SongRepository songRepository;

    private User user;
    private UserRole userRole;
    private Song song;
    private List<Song> songs;

    private UserRole createAndSaveUserRole() {
        userRole = new UserRole();
        userRole.setName("ADMIN");
        userRole = userRoleRepository.save(userRole);
        return userRole;
    }
    private User createUser() {
        UserRole userRole1 = createAndSaveUserRole();
        user = new User();
        user.setUsername("user1");
        user.setPassword("pw1");
        user.setUserRole(userRole1);

        return user;
    }

    private Song createSong () {
        song = new Song();
        song.setLength(502);
        song.setTitle("Orphans Unite");

        return song;
    }

    @Test
    @Transactional
    public void createUserRole_ReurnRole_Success () {
        System.out.println("**User Role Created**");
        UserRole userRole2 = createAndSaveUserRole();
    }

    @Test
    @Transactional
    public void printAllRoles () {
        System.out.println("**print**");
        Iterable<UserRole> userRoles = userRoleRepository.findAll();
        System.out.println(userRoles);
    }

    @Test
    @Transactional
    public void addSong_AddSongToList_Success() {
        User user1 = createUser();

        Song song1 = createSong();
        songs = new ArrayList<>();

        User savedUser = userRepository.save(user1);
        Song savedSong = songRepository.save(song1);
        songs.add(savedSong);

        savedUser.addSong(savedSong);
        savedUser = userRepository.save(savedUser);

        assertThat(savedUser).isEqualToComparingFieldByField(user1);
        assertThat(savedUser.getSongs()).containsSubsequence(songs);
    }

}
