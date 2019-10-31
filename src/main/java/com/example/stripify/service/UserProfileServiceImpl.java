package com.example.stripify.service;

import com.example.stripify.model.User;
import com.example.stripify.model.UserProfile;
import com.example.stripify.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.stripify.util.AuthenticationUtil;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationUtil authenticationUtil;

    @Override
    public UserProfile getUserProfile(String username) {
        Authentication authentication = authenticationUtil.getAuthentication();
        if (authentication != null) {
            if (username.equals(authentication.getName())) return userProfileRepository.findProfileByUsername(username);
        }
        return null; // TODO: HANDLE THIS BETTER, MAYBE WITH EXCEPTION
    }

    @Override
    public UserProfile createUserProfile(UserProfile userProfile, String username) {
        Authentication authentication = authenticationUtil.getAuthentication();
        if (authentication != null) {
            if (username.equals(authentication.getName())) {
                User user = userService.getUser(username);
                if (user.getUserProfile() != null) {
                    userProfile.setId(user.getUserProfile().getId());
                }
                user.setUserProfile(userProfile);
                return userService.updateUser(user).getUserProfile();
            }
        }
        return null; // TODO: HANDLE THIS BETTER, MAYBE WITH EXCEPTION
    }
}
