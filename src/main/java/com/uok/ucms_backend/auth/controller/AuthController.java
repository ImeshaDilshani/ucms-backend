package com.uok.ucms_backend.auth.controller;

import com.uok.ucms_backend.auth.dto.AdminRegistrationRequest;
import com.uok.ucms_backend.auth.dto.AuthResponse;
import com.uok.ucms_backend.auth.dto.LecturerRegistrationRequest;
import com.uok.ucms_backend.auth.dto.LoginRequest;
import com.uok.ucms_backend.auth.dto.RefreshTokenRequest;
import com.uok.ucms_backend.auth.dto.StudentRegistrationRequest;
import com.uok.ucms_backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerStudent(@Valid @RequestBody StudentRegistrationRequest request) {
        AuthResponse response = authService.registerStudent(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register/lecturer")
    public ResponseEntity<AuthResponse> registerLecturer(@Valid @RequestBody LecturerRegistrationRequest request) {
        AuthResponse response = authService.registerLecturer(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
