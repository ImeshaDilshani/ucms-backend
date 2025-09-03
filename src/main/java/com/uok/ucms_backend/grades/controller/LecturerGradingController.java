package com.uok.ucms_backend.grades.controller;

import com.uok.ucms_backend.auth.util.AuthUtil;
import com.uok.ucms_backend.grades.dto.CourseResponse;
import com.uok.ucms_backend.grades.dto.CourseResultResponse;
import com.uok.ucms_backend.grades.dto.EnrolledStudentResponse;
import com.uok.ucms_backend.grades.dto.GradeSubmissionRequest;
import com.uok.ucms_backend.grades.service.CourseGradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lecturers/grading")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class LecturerGradingController {
    
    private final CourseGradingService gradingService;
    
    /**
     * Get all courses available for grading
     */
    @GetMapping("/courses")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<CourseResponse>> getGradableCourses() {
        log.info("Lecturer requesting gradable courses");
        
        List<CourseResponse> courses = gradingService.getGradableCourses();
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Get enrolled students for a course
     */
    @GetMapping("/courses/{courseId}/students")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<EnrolledStudentResponse>> getEnrolledStudents(@PathVariable Long courseId) {
        
        log.info("Lecturer requesting enrolled students for course {}", courseId);
        
        try {
            List<EnrolledStudentResponse> students = gradingService.getEnrolledStudentsForCourse(courseId);
            return ResponseEntity.ok(students);
        } catch (RuntimeException e) {
            log.error("Failed to get enrolled students for course {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Submit grade for a student
     */
    @PostMapping("/submit-grade")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<CourseResultResponse> submitGrade(@Valid @RequestBody GradeSubmissionRequest request) {
        
        Long lecturerId = AuthUtil.getCurrentUserId();
        log.info("Lecturer {} submitting grade for student {} in course {}", 
                lecturerId, request.getStudentId(), request.getCourseId());
        
        try {
            CourseResultResponse result = gradingService.submitGrade(
                lecturerId, 
                request.getStudentId(), 
                request.getCourseId(), 
                request.getMarks(), 
                request.getRemarks()
            );
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to submit grade: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all results for a course
     */
    @GetMapping("/courses/{courseId}/results")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<CourseResultResponse>> getCourseResults(@PathVariable Long courseId) {
        
        Long lecturerId = AuthUtil.getCurrentUserId();
        log.info("Lecturer {} requesting results for course {}", lecturerId, courseId);
        
        try {
            List<CourseResultResponse> results = gradingService.getCourseResults(lecturerId, courseId);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            log.error("Failed to get course results: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Release results for a course
     */
    @PostMapping("/courses/{courseId}/release-results")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> releaseResults(@PathVariable Long courseId) {
        
        Long lecturerId = AuthUtil.getCurrentUserId();
        log.info("Lecturer {} releasing results for course {}", lecturerId, courseId);
        
        try {
            gradingService.releaseResults(lecturerId, courseId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to release results: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
