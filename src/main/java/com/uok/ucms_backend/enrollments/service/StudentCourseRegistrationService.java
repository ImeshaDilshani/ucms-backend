package com.uok.ucms_backend.enrollments.service;

import com.uok.ucms_backend.courses.entity.Course;
import com.uok.ucms_backend.courses.repository.CourseRepository;
import com.uok.ucms_backend.enrollments.dto.CourseRegistrationResponse;
import com.uok.ucms_backend.enrollments.entity.StudentCourseRegistration;
import com.uok.ucms_backend.enrollments.repository.StudentCourseRegistrationRepository;
import com.uok.ucms_backend.users.entity.Student;
import com.uok.ucms_backend.users.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentCourseRegistrationService {
    
    private final StudentCourseRegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    
    /**
     * Register a student for a course
     */
    @Transactional
    public CourseRegistrationResponse registerStudentForCourse(Long studentId, Long courseId) {
        log.info("Registering student {} for course {}", studentId, courseId);
        
        // Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        
        // Find course
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        // Check if course is active
        if (!course.isActive()) {
            throw new RuntimeException("Course is not active: " + course.getCode());
        }
        
        // Check if student is already registered
        if (registrationRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, courseId, StudentCourseRegistration.RegistrationStatus.ACTIVE)) {
            throw new RuntimeException("Student is already registered for this course");
        }
        
        // Check course capacity if maxEnrollments is set
        if (course.getMaxEnrollments() != null && course.getMaxEnrollments() > 0) {
            Long currentRegistrations = registrationRepository.countActiveRegistrationsByCourseId(courseId);
            if (currentRegistrations >= course.getMaxEnrollments()) {
                throw new RuntimeException("Course is full. Maximum enrollment reached.");
            }
        }
        
        // Create registration
        StudentCourseRegistration registration = new StudentCourseRegistration();
        registration.setStudent(student);
        registration.setCourse(course);
        registration.setStudentNumber(student.getIndexNo());
        registration.setCourseCode(course.getCode());
        registration.setCourseTitle(course.getName()); // Using name as title
        registration.setRegisteredAt(LocalDateTime.now());
        registration.setStatus(StudentCourseRegistration.RegistrationStatus.ACTIVE);
        
        StudentCourseRegistration savedRegistration = registrationRepository.save(registration);
        
        // Update course enrollment count
        updateCourseEnrollmentCount(courseId);
        
        log.info("Successfully registered student {} for course {}", student.getIndexNo(), course.getCode());
        
        return convertToResponse(savedRegistration);
    }
    
    /**
     * Unregister a student from a course
     */
    @Transactional
    public void unregisterStudentFromCourse(Long studentId, Long courseId) {
        log.info("Unregistering student {} from course {}", studentId, courseId);
        
        StudentCourseRegistration registration = registrationRepository
            .findByStudentIdAndCourseIdAndStatus(studentId, courseId, StudentCourseRegistration.RegistrationStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException("Student is not registered for this course"));
        
        // Delete the registration record
        registrationRepository.delete(registration);
        
        // Update course enrollment count
        updateCourseEnrollmentCount(courseId);
        
        log.info("Successfully unregistered student {} from course {}", 
                registration.getStudentNumber(), registration.getCourseCode());
    }
    
    /**
     * Get all registered courses for a student by student ID
     */
    @Transactional(readOnly = true)
    public List<CourseRegistrationResponse> getRegisteredCoursesByStudentId(Long studentId) {
        log.info("Fetching registered courses for student ID: {}", studentId);
        
        List<StudentCourseRegistration> registrations = 
            registrationRepository.findActiveRegistrationsByStudentId(studentId);
        
        return registrations.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all registered courses for a student by student number
     */
    @Transactional(readOnly = true)
    public List<CourseRegistrationResponse> getRegisteredCoursesByStudentNumber(String studentNumber) {
        log.info("Fetching registered courses for student number: {}", studentNumber);
        
        List<StudentCourseRegistration> registrations = 
            registrationRepository.findActiveRegistrationsByStudentNumber(studentNumber);
        
        return registrations.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a student is registered for a course
     */
    @Transactional(readOnly = true)
    public boolean isStudentRegisteredForCourse(Long studentId, Long courseId) {
        return registrationRepository.existsByStudentIdAndCourseIdAndStatus(
            studentId, courseId, StudentCourseRegistration.RegistrationStatus.ACTIVE);
    }
    
    /**
     * Update course enrollment count in the course table
     */
    private void updateCourseEnrollmentCount(Long courseId) {
        Long activeRegistrations = registrationRepository.countActiveRegistrationsByCourseId(courseId);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setCurrentEnrollments(activeRegistrations.intValue());
            courseRepository.save(course);
        }
    }
    
    /**
     * Convert registration entity to responseDTO
     */
    private CourseRegistrationResponse convertToResponse(StudentCourseRegistration registration) {
        CourseRegistrationResponse response = new CourseRegistrationResponse();
        response.setId(registration.getId());
        response.setStudentNumber(registration.getStudentNumber());
        response.setCourseCode(registration.getCourseCode());
        response.setCourseTitle(registration.getCourseTitle());
        response.setRegisteredAt(registration.getRegisteredAt());
        response.setStatus(registration.getStatus().toString());
        
        // Get additional course details
        Course course = registration.getCourse();
        if (course != null) {
            response.setCourseName(course.getName());
            response.setCredits(course.getCredits());
            response.setDepartment(course.getDepartment());
            response.setCourseDescription(course.getDescription());
        }
        
        return response;
    }
}
