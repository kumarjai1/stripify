package com.example.stripify.controller;

import com.example.stripify.model.UserRole;
import com.example.stripify.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class UserRoleController {

    @Autowired
    UserRoleService userRoleService;

    @GetMapping("/{rolename}")
    public UserRole getRole(@PathVariable String rolename) {
        return userRoleService.getRole(rolename);
    }

    @PostMapping
    public UserRole createRole(@RequestBody UserRole userRole) {
        return userRoleService.createUserRole(userRole);
    }
}
