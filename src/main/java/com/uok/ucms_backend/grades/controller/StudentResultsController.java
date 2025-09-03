package com.uok.ucms_backend.grades.controller;

import com.uok.ucms_backend.auth.util.AuthUtil;
import com.uok.ucms_backend.grades.dto.CourseResultResponse;
import com.uok.ucms_backend.grades.service.CourseGradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students/results")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentResultsController {
    
    private final CourseGradingService gradingService;
    
    /**
     * Get current student's released results
     */
    @GetMapping("/my-results")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseResultResponse>> getMyResults() {
        
        Long studentId = AuthUtil.getCurrentUserId();
        log.info("Student {} requesting their results", studentId);
        
        try {
            List<CourseResultResponse> results = gradingService.getStudentResults(studentId);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            log.error("Failed to get student results: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get student results by student number (for admin/lecturer use)
     */
    @GetMapping("/student/{studentNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ResponseEntity<List<CourseResultResponse>> getStudentResultsByNumber(@PathVariable String studentNumber) {
        
        log.info("Requesting results for student number: {}", studentNumber);
        
        try {
            List<CourseResultResponse> results = gradingService.getStudentResultsByNumber(studentNumber);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            log.error("Failed to get student results by number: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
