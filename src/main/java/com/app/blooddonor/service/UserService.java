package com.app.blooddonor.service;

import com.app.blooddonor.exception.DuplicateEmailException;
import com.app.blooddonor.exception.ResourceNotFoundException;
import com.app.blooddonor.model.User;
import com.app.blooddonor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository        userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    // ── Register new user ──────────────────────────────────
    @Transactional
    public User register(User user) {
        log.info("Registration attempt for email: {}", user.getEmail());

        if (userRepo.existsByEmail(user.getEmail())) {
            log.warn("Email already exists: {}", user.getEmail());
            throw new DuplicateEmailException(
                "An account with email " + user.getEmail() + " already exists"
            );
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepo.save(user);
        log.info("User registered — id: {}, email: {}", saved.getId(), saved.getEmail());
        return saved;
    }

    // ── Find user by email ─────────────────────────────────
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.debug("Looking up user: {}", email);
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found: " + email
            ));
    }

    // ── Spring Security auth ───────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        log.debug("Spring Security loading user: {}", email);
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found: " + email
            ));
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }
}
