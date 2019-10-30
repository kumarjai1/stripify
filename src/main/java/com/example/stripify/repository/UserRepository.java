package com.example.stripify.repository;

import com.example.stripify.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("FROM User u WHERE u.username = ?1 AND u.password = ?2")
    User login(String username, String password);
}
