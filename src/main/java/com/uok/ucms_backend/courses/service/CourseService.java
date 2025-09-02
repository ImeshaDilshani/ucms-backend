package com.uok.ucms_backend.courses.service;

import com.uok.ucms_backend.courses.dto.CourseCreateRequest;
import com.uok.ucms_backend.courses.dto.CourseResponse;
import com.uok.ucms_backend.courses.dto.CourseUpdateRequest;
import com.uok.ucms_backend.courses.entity.Course;
import com.uok.ucms_backend.courses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    public CourseResponse createCourse(CourseCreateRequest request) {
        log.info("Creating new course with code: {}", request.getCode());
        
        // Check if course code already exists
        if (courseRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Course with code '" + request.getCode() + "' already exists");
        }
        
        Course course = new Course();
        course.setCode(request.getCode().toUpperCase());
        course.setName(request.getName());
        course.setTitle(request.getName()); // Set title same as name for DB compatibility
        course.setCredits(request.getCredits());
        course.setDescription(request.getDescription());
        course.setDepartment(request.getDepartment());
        course.setMaxEnrollments(request.getMaxEnrollments());
        course.setCurrentEnrollments(0);
        course.setActive(true);
        
        // Set prerequisites if provided
        if (request.getPrerequisiteCodes() != null && !request.getPrerequisiteCodes().isEmpty()) {
            Set<Course> prerequisites = new HashSet<>();
            for (String code : request.getPrerequisiteCodes()) {
                Course prereq = courseRepository.findByCode(code.toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Prerequisite course not found with code: " + code));
                prerequisites.add(prereq);
            }
            course.setPrerequisites(prerequisites);
        }
        
        Course savedCourse = courseRepository.save(course);
        log.info("Successfully created course with ID: {}", savedCourse.getId());
        
        return mapToResponse(savedCourse);
    }
    
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        log.info("Updating course with ID: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
        
        course.setName(request.getName());
        course.setTitle(request.getName()); // Set title same as name for DB compatibility
        course.setCredits(request.getCredits());
        course.setDescription(request.getDescription());
        course.setDepartment(request.getDepartment());
        course.setMaxEnrollments(request.getMaxEnrollments());
        
        // Update prerequisites if provided
        if (request.getPrerequisiteCodes() != null) {
            if (request.getPrerequisiteCodes().isEmpty()) {
                course.getPrerequisites().clear();
            } else {
                Set<Course> prerequisites = new HashSet<>();
                for (String code : request.getPrerequisiteCodes()) {
                    Course prereq = courseRepository.findByCode(code.toUpperCase())
                        .orElseThrow(() -> new IllegalArgumentException("Prerequisite course not found with code: " + code));
                    prerequisites.add(prereq);
                }
                
                // Check for circular dependencies
                if (prerequisites.contains(course) || 
                    prerequisites.stream().anyMatch(p -> hasCircularDependency(course, p))) {
                    throw new IllegalArgumentException("Circular prerequisite dependency detected");
                }
                
                course.setPrerequisites(prerequisites);
            }
        }
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Successfully updated course with ID: {}", courseId);
        
        return mapToResponse(updatedCourse);
    }
    
    public void deleteCourse(Long courseId) {
        log.info("Deleting course with ID: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
        
        // Check if course is used as prerequisite by other courses
        if (!course.getDependentCourses().isEmpty()) {
            String dependentCourses = course.getDependentCourses().stream()
                .map(Course::getCode)
                .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                "Cannot delete course. It is a prerequisite for: " + dependentCourses);
        }
        
        courseRepository.delete(course);
        log.info("Successfully deleted course with ID: {}", courseId);
    }
    
    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
        
        return mapToResponse(course);
    }
    
    @Transactional(readOnly = true)
    public CourseResponse getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Course not found with code: " + code));
        
        return mapToResponse(course);
    }
    
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllCourses(String code, String name, Pageable pageable) {
        Page<Course> courses = courseRepository.findWithFilters(code, name, pageable);
        return courses.map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private boolean hasCircularDependency(Course course, Course prerequisite) {
        return hasCircularDependencyRecursive(course, prerequisite, new HashSet<>());
    }
    
    private boolean hasCircularDependencyRecursive(Course target, Course current, Set<Long> visited) {
        if (visited.contains(current.getId())) {
            return false; // Already checked this path
        }
        
        visited.add(current.getId());
        
        for (Course dependency : current.getPrerequisites()) {
            if (dependency.getId().equals(target.getId())) {
                return true; // Circular dependency found
            }
            if (hasCircularDependencyRecursive(target, dependency, visited)) {
                return true;
            }
        }
        
        return false;
    }
    
    private CourseResponse mapToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCode(course.getCode());
        response.setName(course.getName());
        response.setCredits(course.getCredits());
        response.setDescription(course.getDescription());
        response.setDepartment(course.getDepartment());
        response.setMaxEnrollments(course.getMaxEnrollments());
        response.setCurrentEnrollments(course.getCurrentEnrollments());
        response.setIsActive(course.isActive());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        
        // Map prerequisites
        Set<CourseResponse.CoursePrerequisiteResponse> prerequisites = course.getPrerequisites()
            .stream()
            .map(prereq -> new CourseResponse.CoursePrerequisiteResponse(
                prereq.getId(),
                prereq.getCode(),
                prereq.getName(),
                prereq.getCredits()
            ))
            .collect(Collectors.toSet());
        
        response.setPrerequisites(prerequisites);
        
        return response;
    }
    
    // Student-specific methods
    
    /**
     * Get available courses for students with pagination and filtering
     * Only returns active courses
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAvailableCoursesForStudents(String code, String name, String department, Pageable pageable) {
        log.info("Fetching available courses for students with filters - code: {}, name: {}, department: {}", code, name, department);
        
        Page<Course> coursePage = courseRepository.findActiveCoursesWithFilters(code, name, department, pageable);
        return coursePage.map(this::mapToResponse);
    }
    
    /**
     * Get all available courses for students without pagination
     * Only returns active courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllAvailableCoursesForStudents() {
        log.info("Fetching all available courses for students");
        
        List<Course> courses = courseRepository.findByActiveTrue();
        return courses.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get course details for student by ID
     * Only returns active courses
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourseForStudent(Long courseId) {
        log.info("Fetching course details for student - ID: {}", courseId);
        
        Course course = courseRepository.findByIdAndActiveTrue(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found or not available: " + courseId));
        
        return mapToResponse(course);
    }
    
    /**
     * Get course details for student by code
     * Only returns active courses
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourseByCodeForStudent(String code) {
        log.info("Fetching course details for student - code: {}", code);
        
        Course course = courseRepository.findByCodeAndActiveTrue(code.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Course not found or not available: " + code));
        
        return mapToResponse(course);
    }
    
    /**
     * Search courses for students by keyword
     * Searches in course name and description, only returns active courses
     */
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchCoursesForStudents(String keyword, Pageable pageable) {
        log.info("Searching courses for students with keyword: {}", keyword);
        
        Page<Course> coursePage = courseRepository.searchActiveCoursesbyKeyword(keyword, pageable);
        return coursePage.map(this::mapToResponse);
    }
    
    /**
     * Get courses by department for students
     * Only returns active courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByDepartmentForStudents(String department) {
        log.info("Fetching courses for students by department: {}", department);
        
        List<Course> courses = courseRepository.findByDepartmentAndActiveTrueOrderByCode(department);
        return courses.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get courses by credit value for students
     * Only returns active courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByCreditValueForStudents(Integer credits) {
        log.info("Fetching courses for students by credits: {}", credits);
        
        List<Course> courses = courseRepository.findByCreditsAndActiveTrueOrderByCode(credits);
        return courses.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
}
