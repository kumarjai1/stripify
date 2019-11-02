package com.example.stripify.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.junit4.SpringRunner;

import com.example.stripify.model.Song;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.stripify.config.JwtRequestFilter;
import com.example.stripify.config.SecurityConfig;
import com.example.stripify.model.User;
import com.example.stripify.model.UserRole;
import com.example.stripify.service.UserService;
import com.example.stripify.util.JwtResponse;
import com.example.stripify.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserController.class, SecurityConfig.class, JwtRequestFilter.class})
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    JwtUtil jwtUtil;

    private User user;
    private UserRole userRole;
    private ObjectMapper objectMapper;
    private String generatedToken;
    private Song song;
    private List<Song> songs;

    public UserControllerTest() {
        user = new User();
        userRole = new UserRole();
        song = new Song();
        songs = new ArrayList<>();
        user.setUserRole(userRole);
        objectMapper = new ObjectMapper();
        generatedToken = "lkajsd3lr3lkfnflan3llk3lkahl";
    }


    @BeforeEach
//    @Before
    public void init() {
        System.out.println("Method Run");
        user.setId(1L);
        userRole.setName("ROLE_ADMIN");
        user.setUsername("user1");
        user.setPassword("pw1");
        generatedToken = "lkajsd3lr3lkfnflan3llk3lkahl";

        song.setId(1L);
        song.setLength(500);
        song.setTitle("Orphans United");
        songs.add(song);
        user.setSongs(songs);

    }

    @Test
    public void signup_ValidNewUser_ReturnsJwtAndUsernameSuccessfully() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        JwtResponse jwtResponse = new JwtResponse(generatedToken, user.getUsername());
        when(userService.signup(any())).thenReturn(jwtResponse);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jwtResponse)));
    }

    @Test
    public void signup_BlankUsername_Failure() throws Exception {
        user.setUsername("");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    public void login_ValidUser_ReturnsJsonAndUsername() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));

        when(userService.login(any())).thenReturn(generatedToken);
        JwtResponse jwtResponse = new JwtResponse(userService.login(user), user.getUsername());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jwtResponse)));
    }

    @Test
    @WithMockUser(username = "batman", roles= {"ADMIN"})
    public void listUsers_AuthortizedUser_ReturnsListOfUsers() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/list")
                .accept(MediaType.APPLICATION_JSON);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userService.listUsers()).thenReturn(userList);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userList)));

    }

    @Test
    @WithMockUser(username = "batman", roles= {"USER"})
    public void listUsers_UnauthortizedUser_ReturnsForbiddenMsg() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user/list")
                .accept(MediaType.APPLICATION_JSON);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userService.listUsers()).thenReturn(userList);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
//                .andExpect(content().json(objectMapper.writeValueAsString(userList)));

    }

    @Test //admin has access to adding song for anyone
    @WithMockUser(username = "user3", roles={"ADMIN"})
    public void addSong_AddSongToUser_ListOfUserSongs () throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/"+user.getUsername()+"/"+song.getId());

        when(userService.addSong(any(), any())).thenReturn(songs);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(songs)));
    }

    @Test //admin has access to adding song for anyone
    @WithMockUser(username = "user2")
    public void addSong_UnauthorizedUser_ForbiddenError () throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/"+user.getUsername()+"/"+song.getId());

        when(userService.addSong(any(), any())).thenReturn(songs);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test //admin has access to adding song for anyone
    @WithMockUser(username = "user1")
    public void addSong_ActualUser_SongList () throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/"+user.getUsername()+"/"+song.getId());

        when(userService.addSong(any(), any())).thenReturn(songs);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(songs)));
    }

    @Test
    @WithMockUser(username="user1", roles = {"ADMIN"})
    public void deleteUserById_AuthorizedUser_DeletedUserId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/"+user.getId());

        when(userService.deleteById(any())).thenReturn(user.getId());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string(user.getId().toString()));
    }

    @Test
    @WithMockUser(username="user1")
    public void deleteUserById_UnauthorizedUser_Forbidden() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/user/"+user.getId());

        when(userService.deleteById(any())).thenReturn(user.getId());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }




}
