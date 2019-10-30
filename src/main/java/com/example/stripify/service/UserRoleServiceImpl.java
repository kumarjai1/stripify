package com.example.stripify.service;

import com.example.stripify.model.UserRole;
import com.example.stripify.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public UserRole createUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public UserRole getRole(String rolename) {
        return userRoleRepository.findByName(rolename);
    }
}
