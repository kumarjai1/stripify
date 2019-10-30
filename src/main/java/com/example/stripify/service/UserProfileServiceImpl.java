package com.example.stripify.service;

import com.example.stripify.model.User;
import com.example.stripify.model.UserProfile;
import com.example.stripify.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserService userService;

    @Override
    public UserProfile getUserProfile(String username) {
        return userProfileRepository.findProfileByUsername(username);
    }

    @Override
    public UserProfile createUserProfile(UserProfile userProfile, String username) {
        User user = userService.getUser(username);
        user.setUserProfile(userProfile);
        return userService.updateUser(user).getUserProfile();
    }
}
