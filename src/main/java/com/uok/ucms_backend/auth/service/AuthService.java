package com.uok.ucms_backend.auth.service;

import com.uok.ucms_backend.auth.dto.AdminRegistrationRequest;
import com.uok.ucms_backend.auth.dto.AuthResponse;
import com.uok.ucms_backend.auth.dto.LecturerRegistrationRequest;
import com.uok.ucms_backend.auth.dto.LoginRequest;
import com.uok.ucms_backend.auth.dto.RefreshTokenRequest;
import com.uok.ucms_backend.auth.dto.StudentRegistrationRequest;
import com.uok.ucms_backend.auth.entity.RefreshToken;
import com.uok.ucms_backend.auth.repository.RefreshTokenRepository;
import com.uok.ucms_backend.auth.util.JwtUtils;
import com.uok.ucms_backend.users.entity.Lecturer;
import com.uok.ucms_backend.users.entity.Student;
import com.uok.ucms_backend.users.entity.User;
import com.uok.ucms_backend.users.repository.LecturerRepository;
import com.uok.ucms_backend.users.repository.StudentRepository;
import com.uok.ucms_backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Value("${app.lecturer-secret-key}")
    private String lecturerSecretKey;
    
    @Value("${app.admin-secret-key}")
    private String adminSecretKey;
    
    @Transactional
    public AuthResponse registerStudent(StudentRegistrationRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        if (studentRepository.existsByIndexNo(request.getIndexNo())) {
            throw new RuntimeException("Index number is already registered!");
        }
        
        // Create new student
        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setEmail(request.getEmail());
        student.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        student.setRole(User.UserRole.STUDENT);
        student.setIndexNo(request.getIndexNo());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setProgram(request.getProgram());
        student.setYear(request.getYear());
        
        Student savedStudent = studentRepository.save(student);
        
        // Generate tokens
        String accessToken = jwtUtils.generateAccessToken(savedStudent);
        String refreshToken = createRefreshToken(savedStudent);
        
        return createAuthResponse(savedStudent, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse registerLecturer(LecturerRegistrationRequest request) {
        // Validate lecturer secret key
        if (!lecturerSecretKey.equals(request.getLecturerSecretKey())) {
            throw new RuntimeException("Invalid lecturer secret key!");
        }
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        if (lecturerRepository.existsByStaffNo(request.getStaffNo())) {
            throw new RuntimeException("Staff number is already registered!");
        }
        
        // Create new lecturer
        Lecturer lecturer = new Lecturer();
        lecturer.setUsername(request.getUsername());
        lecturer.setEmail(request.getEmail());
        lecturer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        lecturer.setRole(User.UserRole.LECTURER);
        lecturer.setStaffNo(request.getStaffNo());
        lecturer.setFirstName(request.getFirstName());
        lecturer.setLastName(request.getLastName());
        lecturer.setDepartment(request.getDepartment());
        
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        
        // Generate tokens
        String accessToken = jwtUtils.generateAccessToken(savedLecturer);
        String refreshToken = createRefreshToken(savedLecturer);
        
        return createAuthResponse(savedLecturer, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse registerAdmin(AdminRegistrationRequest request) {
        // Validate admin secret key
        if (!adminSecretKey.equals(request.getAdminKey())) {
            throw new RuntimeException("Invalid admin secret key!");
        }
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new admin
        User admin = new User();
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setRole(User.UserRole.ADMIN);
        
        User savedAdmin = userRepository.save(admin);
        
        // Generate tokens
        String accessToken = jwtUtils.generateAccessToken(savedAdmin);
        String refreshToken = createRefreshToken(savedAdmin);
        
        return createAuthResponse(savedAdmin, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);
        
        return createAuthResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token expired");
        }
        
        User user = storedToken.getUser();
        String newAccessToken = jwtUtils.generateAccessToken(user);
        
        return createAuthResponse(user, newAccessToken, refreshToken);
    }
    
    @Transactional
    private String createRefreshToken(User user) {
        // Delete existing refresh tokens for this user (using entity-based deletion)
        try {
            refreshTokenRepository.deleteByUserId(user.getId());
            refreshTokenRepository.flush(); // Force the delete to execute
        } catch (Exception e) {
            log.warn("Could not delete existing refresh tokens for user {}: {}", user.getUsername(), e.getMessage());
            // Continue with creating new token even if deletion fails
        }
        
        String token = jwtUtils.generateRefreshToken(user);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days
        
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    private AuthResponse createAuthResponse(User user, String accessToken, String refreshToken) {
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().name()
        );
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            "Bearer",
            jwtUtils.getJwtExpirationMs() / 1000, // Convert to seconds
            userInfo
        );
    }
}
