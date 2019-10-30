package com.example.stripify.repository;

import com.example.stripify.model.UserRole;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {

    UserRole findByName(String name);
}
