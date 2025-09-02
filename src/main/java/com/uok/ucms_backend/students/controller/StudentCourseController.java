package com.uok.ucms_backend.students.controller;

import com.uok.ucms_backend.courses.dto.CourseResponse;
import com.uok.ucms_backend.courses.service.CourseService;
import com.uok.ucms_backend.students.dto.StudentCourseViewResponse;
import com.uok.ucms_backend.students.service.StudentCourseService;
import com.uok.ucms_backend.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students/courses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentCourseController {
    
    private final CourseService courseService;
    private final StudentCourseService studentCourseService;
    
    /**
     * Get all available courses for students with pagination and filtering
     */
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<StudentCourseViewResponse>> getAllAvailableCourses(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Student requesting available courses - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Get courses from course service
        Page<CourseResponse> coursePage = courseService.getAvailableCoursesForStudents(code, name, department, pageable);
        
        // Convert to student view with additional information
        Page<StudentCourseViewResponse> studentCoursePage = studentCourseService.convertToStudentViews(coursePage);
        
        PageResponse<StudentCourseViewResponse> response = PageResponse.<StudentCourseViewResponse>builder()
            .content(studentCoursePage.getContent())
            .page(studentCoursePage.getNumber())
            .size(studentCoursePage.getSize())
            .totalElements(studentCoursePage.getTotalElements())
            .totalPages(studentCoursePage.getTotalPages())
            .first(studentCoursePage.isFirst())
            .last(studentCoursePage.isLast())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all available courses for students without pagination
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<StudentCourseViewResponse>> getAllAvailableCoursesSimple() {
        log.info("Student requesting all available courses without pagination");
        List<CourseResponse> courses = courseService.getAllAvailableCoursesForStudents();
        List<StudentCourseViewResponse> studentCourses = studentCourseService.convertToStudentViews(courses);
        return ResponseEntity.ok(studentCourses);
    }
    
    /**
     * Get course details by ID (Student can view)
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentCourseViewResponse> getCourseDetails(@PathVariable Long courseId) {
        log.info("Student requesting course details for ID: {}", courseId);
        CourseResponse courseResponse = courseService.getCourseForStudent(courseId);
        StudentCourseViewResponse studentView = studentCourseService.convertToStudentView(courseResponse);
        return ResponseEntity.ok(studentView);
    }
    
    /**
     * Get course details by code (Student can view)
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentCourseViewResponse> getCourseDetailsByCode(@PathVariable String code) {
        log.info("Student requesting course details for code: {}", code);
        CourseResponse courseResponse = courseService.getCourseByCodeForStudent(code);
        StudentCourseViewResponse studentView = studentCourseService.convertToStudentView(courseResponse);
        return ResponseEntity.ok(studentView);
    }
    
    /**
     * Search courses by keyword in name or description
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<StudentCourseViewResponse>> searchCourses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Student searching courses with keyword: {}", keyword);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseResponse> coursePage = courseService.searchCoursesForStudents(keyword, pageable);
        
        Page<StudentCourseViewResponse> studentCoursePage = studentCourseService.convertToStudentViews(coursePage);
        
        PageResponse<StudentCourseViewResponse> response = PageResponse.<StudentCourseViewResponse>builder()
            .content(studentCoursePage.getContent())
            .page(studentCoursePage.getNumber())
            .size(studentCoursePage.getSize())
            .totalElements(studentCoursePage.getTotalElements())
            .totalPages(studentCoursePage.getTotalPages())
            .first(studentCoursePage.isFirst())
            .last(studentCoursePage.isLast())
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get courses by department for students
     */
    @GetMapping("/department/{department}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<StudentCourseViewResponse>> getCoursesByDepartment(@PathVariable String department) {
        log.info("Student requesting courses for department: {}", department);
        List<CourseResponse> courses = courseService.getCoursesByDepartmentForStudents(department);
        List<StudentCourseViewResponse> studentCourses = studentCourseService.convertToStudentViews(courses);
        return ResponseEntity.ok(studentCourses);
    }
    
    /**
     * Get courses by credit value for students
     */
    @GetMapping("/credits/{credits}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<StudentCourseViewResponse>> getCoursesByCredits(@PathVariable Integer credits) {
        log.info("Student requesting courses with {} credits", credits);
        List<CourseResponse> courses = courseService.getCoursesByCreditValueForStudents(credits);
        List<StudentCourseViewResponse> studentCourses = studentCourseService.convertToStudentViews(courses);
        return ResponseEntity.ok(studentCourses);
    }
    
    /**
     * Get courses available for enrollment (courses with available capacity)
     */
    @GetMapping("/enrollment/available")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<StudentCourseViewResponse>> getCoursesAvailableForEnrollment() {
        log.info("Student requesting courses available for enrollment");
        List<StudentCourseViewResponse> availableCourses = studentCourseService.getAvailableCoursesForEnrollment();
        return ResponseEntity.ok(availableCourses);
    }
    
    /**
     * Get courses available for enrollment by department
     */
    @GetMapping("/enrollment/available/department/{department}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<StudentCourseViewResponse>> getCoursesAvailableForEnrollmentByDepartment(
            @PathVariable String department) {
        log.info("Student requesting courses available for enrollment in department: {}", department);
        List<StudentCourseViewResponse> availableCourses = 
            studentCourseService.getAvailableCoursesForEnrollmentByDepartment(department);
        return ResponseEntity.ok(availableCourses);
    }
    
    /**
     * Check if a course is available for enrollment
     */
    @GetMapping("/{courseId}/enrollment/check")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> checkCourseEnrollmentAvailability(@PathVariable Long courseId) {
        log.info("Student checking enrollment availability for course ID: {}", courseId);
        boolean available = studentCourseService.canEnrollInCourse(courseId);
        return ResponseEntity.ok(available);
    }
}
