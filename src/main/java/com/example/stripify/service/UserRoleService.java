package com.example.stripify.service;

import com.example.stripify.model.UserRole;

public interface UserRoleService {

    UserRole createUserRole(UserRole userRole);
    UserRole getRole(String rolename);
}
