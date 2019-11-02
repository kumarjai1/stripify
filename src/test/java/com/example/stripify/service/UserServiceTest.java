package com.example.stripify.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.example.stripify.model.Song;
import com.example.stripify.model.User;
import com.example.stripify.model.UserRole;
import com.example.stripify.repository.UserRepository;
import com.example.stripify.util.JwtResponse;
import com.example.stripify.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

//    @Rule
//    public MockitoRule mockito = MockitoJUnit.rule();

    private User user1;
    private Song song1;
    private UserRole userRole;
    private List<Song> songs;
    private String encodedPassword;
    private String generatedToken;
    private ObjectMapper objectMapper;

    public UserServiceTest() {
        user1 = new User();
        song1 = new Song();
        userRole = new UserRole();
        songs = new ArrayList<>();
        encodedPassword = "asdjkhfjlejhalj3hflkajhf3khakjfhja";
        generatedToken = "alkjsdlfajlekjlj3lkajflkjaj.flajl3jkldjksfla.lasdjfljelfakje";
        objectMapper = new ObjectMapper();
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void init() {
        userRole.setId(1);
        userRole.setName("ROLE_ADMIN");

        songs.add(song1);

        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("pw1");
        user1.setUserRole(userRole);
        user1.setSongs(songs);

        song1.setId(1L);
        song1.setTitle("Orphans Unite");
        song1.setLength(502);
    }

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserRoleService userRoleService;

    @Mock
    SongService songService;

    @Mock
    PasswordEncoder bCryptPasswordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @Test
    public void signup_ValidUser_Success() throws JsonProcessingException {
        when(userRoleService.getRole(anyString())).thenReturn(userRole);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any())).thenReturn(user1);
        when(jwtUtil.generateToken(any())).thenReturn(generatedToken);
        when(userRepository.findByUsername(anyString())).thenReturn(user1);

        JwtResponse jwtResponse = new JwtResponse(generatedToken, user1.getUsername());
        JwtResponse returnedJwtResponse = userService.signup(user1);

        assertThat(returnedJwtResponse).isNotNull();
        assertThat(returnedJwtResponse).isEqualToComparingFieldByField(jwtResponse);
        verify(jwtUtil, times(1)).generateToken(any());
    }

    @Test
    public void signup_DuplicateUser_Failure() {
        user1.setUsername("duplicateUsername");
        when(userRoleService.getRole(anyString())).thenReturn(userRole);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any()))
                .thenThrow(new ConstraintViolationException("could not execute statement", new SQLException(), "username"));

        Throwable thrown = catchThrowable(() -> userService.signup(user1));
        assertThat(thrown).isInstanceOf(ConstraintViolationException.class);
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    public void login_ValidUser_Success () {
        when(userRepository.findByUsername(any())).thenReturn(user1);
        when(bCryptPasswordEncoder.encode(any())).thenReturn(encodedPassword);
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn(generatedToken);

        String token = userService.login(user1);
        assertThat(token).isNotNull();
        assertThat(token).isEqualTo(generatedToken);

    }

    @Test
    @WithMockUser(username = "user1")
    public void addSong_ValidUser_SuccessSongList () throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(user1);
        when(songService.getSong(anyLong())).thenReturn(song1);
        when(userRepository.save(user1)).thenReturn(user1);

        List<Song> savedList = userService.addSong(user1.getUsername(), song1.getId());

        assertThat(savedList).isNotNull();
        assertThat(savedList).containsSubsequence(songs);

        verify(userRepository, times(1)).save(user1);
    }

    @Test
    @WithMockUser(username = "user1")
    public void addSong_SongNotValid_ThrowException () throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(user1);
        when(songService.getSong(anyLong())).thenReturn(null);

        when(userRepository.save(user1)).thenReturn(user1);

        Throwable thrown = catchThrowable(() -> userService.addSong(user1.getUsername(), 2L));

        assertThat(thrown).isInstanceOf(Exception.class);

        verify(userRepository, never()).save(user1);
    }


}
