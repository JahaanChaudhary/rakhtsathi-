package com.app.blooddonor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Separate file to avoid circular dependency:
// SecurityConfig needs UserService
// UserService needs BCryptPasswordEncoder
// If BCryptPasswordEncoder were inside SecurityConfig → circular loop
@Configuration
public class PasswordConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
