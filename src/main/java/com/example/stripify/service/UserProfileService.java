package com.example.stripify.service;

import com.example.stripify.model.UserProfile;

public interface UserProfileService {

    UserProfile getUserProfile(String username);
    UserProfile createUserProfile(UserProfile userProfile, String username);
}
