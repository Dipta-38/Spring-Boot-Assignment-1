package com.example.university.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void passwordEncoder() {
        assertNotNull(passwordEncoder, "PasswordEncoder bean should be loaded");

        String rawPassword = "test123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword),
                "Encoded password should match the raw password");
    }

    @Test
    void securityFilterChain() {
        assertNotNull(securityFilterChain, "SecurityFilterChain bean should be loaded");
        assertFalse(securityFilterChain.getFilters().isEmpty(),
                "SecurityFilterChain should contain filters");
    }
}