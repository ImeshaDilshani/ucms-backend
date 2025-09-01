package com.uok.ucms_backend.config;

import com.uok.ucms_backend.users.entity.User;
import com.uok.ucms_backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin if no admin exists
        boolean adminExists = userRepository.existsByRole(User.UserRole.ADMIN);
        
        if (!adminExists) {
            log.info("No admin user found. Creating default admin...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@university.edu");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            
            userRepository.save(admin);
            log.info("Default admin created successfully!");
            log.info("Admin credentials - Username: admin, Password: admin123");
        }
        
        long userCount = userRepository.count();
        log.info("Total users in database: {}", userCount);
    }
}