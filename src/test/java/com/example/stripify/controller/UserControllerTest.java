package com.example.stripify.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.junit4.SpringRunner;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

    public UserControllerTest() {
        user = new User();
        userRole = new UserRole();
        user.setUserRole(userRole);
        objectMapper = new ObjectMapper();
        generatedToken = "lkajsd3lr3lkfnflan3llk3lkahl";
    }


    @BeforeEach
//    @Before
    public void init() {
        System.out.println("!!!!!!!!!!!!!!!!!!!");
        userRole.setName("ROLE_ADMIN");
        user.setUsername("user1");
        user.setPassword("pw1");
        generatedToken = "lkajsd3lr3lkfnflan3llk3lkahl";
    }

    @Test
    public void signup_ValidNewUser_ReturnsJwtAndUsernameSuccessfully() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user));
        System.out.println("user: " + user);
        System.out.println(user.getUsername());
        System.out.println(objectMapper.writeValueAsString(user));

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
        System.out.println("user: " + user);
        System.out.println(user.getUsername());
        System.out.println(objectMapper.writeValueAsString(user));

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }


}
