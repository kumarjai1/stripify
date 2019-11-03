package com.example.stripify.config;

import com.example.stripify.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("com.example.stripify.repository")
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class RepoConfig {


}
