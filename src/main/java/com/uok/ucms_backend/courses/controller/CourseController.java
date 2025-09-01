package com.uok.ucms_backend.courses.controller;

import com.uok.ucms_backend.courses.dto.CourseCreateRequest;
import com.uok.ucms_backend.courses.dto.CourseResponse;
import com.uok.ucms_backend.courses.dto.CourseUpdateRequest;
import com.uok.ucms_backend.courses.service.CourseService;
import com.uok.ucms_backend.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {
    
    private final CourseService courseService;
    
    /**
     * Create a new course (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        log.info("Creating new course with code: {}", request.getCode());
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Update an existing course (Admin only)
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        log.info("Updating course with ID: {}", courseId);
        CourseResponse response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete a course (Admin only)
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        log.info("Deleting course with ID: {}", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get course by ID (Admin only)
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long courseId) {
        CourseResponse response = courseService.getCourse(courseId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get course by code (Admin only)
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> getCourseByCode(@PathVariable String code) {
        CourseResponse response = courseService.getCourseByCode(code);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all courses with pagination and filtering (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CourseResponse>> getAllCourses(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseResponse> coursePage = courseService.getAllCourses(code, title, pageable);
        
        PageResponse<CourseResponse> response = PageResponse.<CourseResponse>builder()
            .content(coursePage.getContent())
            .page(coursePage.getNumber())
            .size(coursePage.getSize())
            .totalElements(coursePage.getTotalElements())
            .totalPages(coursePage.getTotalPages())
            .first(coursePage.isFirst())
            .last(coursePage.isLast())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all courses without pagination (Admin only)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourseResponse>> getAllCoursesSimple() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
}
