package com.example.portfolio.config;

import com.example.portfolio.entity.Admin;
import com.example.portfolio.repository.AdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads admin accounts from the database for Spring Security's form login.
 * Without this bean, Spring Security silently falls back to its own default
 * single account with a random generated password instead of the Admin rows
 * seeded by DataInitializer / edited from Admin -> Profile.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public CustomUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No admin found with username: " + username));

        return User.withUsername(admin.getUsername())
                .password(admin.getPassword())
                .authorities(admin.getRole())
                .build();
    }
}
