package com.uok.ucms_backend.enrollments.controller;

import com.uok.ucms_backend.auth.util.AuthUtil;
import com.uok.ucms_backend.enrollments.dto.CourseRegistrationRequest;
import com.uok.ucms_backend.enrollments.dto.CourseRegistrationResponse;
import com.uok.ucms_backend.enrollments.service.StudentCourseRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students/course-registrations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentCourseRegistrationController {
    
    private final StudentCourseRegistrationService registrationService;
    
    /**
     * Register for a course
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CourseRegistrationResponse> registerForCourse(
            @Valid @RequestBody CourseRegistrationRequest request) {
        
        Long studentId = AuthUtil.getCurrentUserId();
        log.info("Student {} attempting to register for course {}", studentId, request.getCourseId());
        
        try {
            CourseRegistrationResponse response = registrationService
                .registerStudentForCourse(studentId, request.getCourseId());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Registration failed for student {} and course {}: {}", 
                    studentId, request.getCourseId(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Unregister from a course
     */
    @DeleteMapping("/unregister/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> unregisterFromCourse(@PathVariable Long courseId) {
        
        Long studentId = AuthUtil.getCurrentUserId();
        log.info("Student {} attempting to unregister from course {}", studentId, courseId);
        
        try {
            registrationService.unregisterStudentFromCourse(studentId, courseId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Unregistration failed for student {} and course {}: {}", 
                    studentId, courseId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all registered courses for the current student
     */
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseRegistrationResponse>> getMyRegisteredCourses() {
        
        Long studentId = AuthUtil.getCurrentUserId();
        log.info("Fetching registered courses for student {}", studentId);
        
        List<CourseRegistrationResponse> registrations = 
            registrationService.getRegisteredCoursesByStudentId(studentId);
        
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * Check if current student is registered for a specific course
     */
    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> checkRegistrationStatus(@PathVariable Long courseId) {
        
        Long studentId = AuthUtil.getCurrentUserId();
        boolean isRegistered = registrationService.isStudentRegisteredForCourse(studentId, courseId);
        
        return ResponseEntity.ok(isRegistered);
    }
    
    /**
     * Get registered courses by student number (for admin/lecturer use)
     */
    @GetMapping("/student/{studentNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ResponseEntity<List<CourseRegistrationResponse>> getRegisteredCoursesByStudentNumber(
            @PathVariable String studentNumber) {
        
        log.info("Fetching registered courses for student number: {}", studentNumber);
        
        List<CourseRegistrationResponse> registrations = 
            registrationService.getRegisteredCoursesByStudentNumber(studentNumber);
        
        return ResponseEntity.ok(registrations);
    }
}
