package com.uok.ucms_backend.grades.service;

import com.uok.ucms_backend.courses.entity.Course;
import com.uok.ucms_backend.courses.repository.CourseRepository;
import com.uok.ucms_backend.enrollments.entity.StudentCourseRegistration;
import com.uok.ucms_backend.enrollments.repository.StudentCourseRegistrationRepository;
import com.uok.ucms_backend.grades.dto.CourseResponse;
import com.uok.ucms_backend.grades.dto.CourseResultResponse;
import com.uok.ucms_backend.grades.dto.EnrolledStudentResponse;
import com.uok.ucms_backend.grades.entity.CourseResult;
import com.uok.ucms_backend.grades.repository.CourseResultRepository;
import com.uok.ucms_backend.users.entity.Lecturer;
import com.uok.ucms_backend.users.entity.Student;
import com.uok.ucms_backend.users.repository.LecturerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseGradingService {
    
    private final CourseResultRepository courseResultRepository;
    private final StudentCourseRegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    
    /**
     * Get all courses available for grading
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getGradableCourses() {
        log.info("Fetching gradable courses");
        List<Course> courses = courseResultRepository.findGradableCourses();
        return courses.stream()
                .map(this::convertToCourseResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get enrolled students for a course
     */
    @Transactional(readOnly = true)
    public List<EnrolledStudentResponse> getEnrolledStudentsForCourse(Long courseId) {
        log.info("Fetching enrolled students for course ID: {}", courseId);
        
        List<StudentCourseRegistration> registrations = 
            registrationRepository.findActiveRegistrationsByStudentNumber(null); // This needs to be updated
        
        // Better approach: use the existing query
        List<StudentCourseRegistration> courseRegistrations = 
            courseResultRepository.findActiveRegistrationsByCourseId(courseId);
        
        return courseRegistrations.stream()
            .map(this::convertToEnrolledStudentResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Submit or update grade for a student
     */
    @Transactional
    public CourseResultResponse submitGrade(Long lecturerId, Long studentId, Long courseId, 
                                          BigDecimal marks, String remarks) {
        log.info("Submitting grade for student {} in course {} by lecturer {}", 
                studentId, courseId, lecturerId);
        
        // Find or create result
        Optional<CourseResult> existingResult = courseResultRepository.findByStudentIdAndCourseId(studentId, courseId);
        CourseResult result;
        
        if (existingResult.isPresent()) {
            result = existingResult.get();
            log.info("Updating existing grade for student {}", studentId);
        } else {
            result = new CourseResult();
            log.info("Creating new grade for student {}", studentId);
        }
        
        // Get entities
        Student student = getStudentFromRegistration(studentId, courseId);
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
            .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        
        // Set values
        result.setStudent(student);
        result.setCourse(course);
        result.setLecturer(lecturer);
        result.setStudentNumber(student.getIndexNo());
        result.setCourseCode(course.getCode());
        result.setCourseName(course.getName());
        result.setMarks(marks);
        result.setGrade(CourseResult.Grade.fromMarks(marks.doubleValue()));
        result.setRemarks(remarks);
        result.setGradedAt(LocalDateTime.now());
        result.setIsReleased(false); // Not released by default
        
        CourseResult savedResult = courseResultRepository.save(result);
        
        log.info("Grade submitted successfully for student {} in course {}", studentId, courseId);
        return convertToResponse(savedResult);
    }
    
    /**
     * Release results for a course
     */
    @Transactional
    public void releaseResults(Long lecturerId, Long courseId) {
        log.info("Releasing results for course {} by lecturer {}", courseId, lecturerId);
        
        List<CourseResult> results = courseResultRepository.findResultsByCourseIdAndLecturerId(courseId, lecturerId);
        
        for (CourseResult result : results) {
            if (!result.getIsReleased()) {
                result.setIsReleased(true);
                result.setReleasedAt(LocalDateTime.now());
            }
        }
        
        courseResultRepository.saveAll(results);
        
        log.info("Released {} results for course {}", results.size(), courseId);
    }
    
    /**
     * Get results for a course (lecturer view)
     */
    @Transactional(readOnly = true)
    public List<CourseResultResponse> getCourseResults(Long lecturerId, Long courseId) {
        log.info("Fetching course results for course {} by lecturer {}", courseId, lecturerId);
        
        List<CourseResult> results = courseResultRepository.findResultsByCourseIdAndLecturerId(courseId, lecturerId);
        
        return results.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get student's released results
     */
    @Transactional(readOnly = true)
    public List<CourseResultResponse> getStudentResults(Long studentId) {
        log.info("Fetching results for student {}", studentId);
        
        List<CourseResult> results = courseResultRepository.findReleasedResultsByStudentId(studentId);
        
        return results.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Get student's released results by student number
     */
    @Transactional(readOnly = true)
    public List<CourseResultResponse> getStudentResultsByNumber(String studentNumber) {
        log.info("Fetching results for student number {}", studentNumber);
        
        List<CourseResult> results = courseResultRepository.findReleasedResultsByStudentNumber(studentNumber);
        
        return results.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Helper method to get student from registration
     */
    private Student getStudentFromRegistration(Long studentId, Long courseId) {
        Optional<StudentCourseRegistration> registration = 
            registrationRepository.findByStudentIdAndCourseIdAndStatus(
                studentId, courseId, StudentCourseRegistration.RegistrationStatus.ACTIVE);
        
        if (registration.isEmpty()) {
            throw new RuntimeException("Student is not enrolled in this course");
        }
        
        return registration.get().getStudent();
    }
    
    /**
     * Convert registration to enrolled student response
     */
    private EnrolledStudentResponse convertToEnrolledStudentResponse(StudentCourseRegistration registration) {
        Student student = registration.getStudent();
        
        EnrolledStudentResponse response = new EnrolledStudentResponse();
        response.setStudentId(student.getId());
        response.setStudentNumber(student.getIndexNo());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setFullName(student.getFirstName() + " " + student.getLastName());
        response.setEmail(student.getEmail());
        response.setProgram(student.getProgram());
        response.setYear(student.getYear());
        
        // Check if grade exists
        Optional<CourseResult> existingResult = courseResultRepository
            .findByStudentIdAndCourseId(student.getId(), registration.getCourse().getId());
        
        response.setHasGrade(existingResult.isPresent());
        if (existingResult.isPresent()) {
            response.setExistingResult(convertToResponse(existingResult.get()));
        }
        
        return response;
    }
    
    /**
     * Convert entity to response
     */
    private CourseResultResponse convertToResponse(CourseResult result) {
        CourseResultResponse response = new CourseResultResponse();
        response.setId(result.getId());
        response.setStudentNumber(result.getStudentNumber());
        response.setStudentName(result.getStudent().getFirstName() + " " + result.getStudent().getLastName());
        response.setCourseCode(result.getCourseCode());
        response.setCourseName(result.getCourseName());
        response.setCredits(result.getCourse().getCredits());
        response.setMarks(result.getMarks());
        response.setGrade(result.getGrade() != null ? result.getGrade().name() : null);
        response.setGradeDisplay(result.getGrade() != null ? result.getGrade().getDisplayName() : null);
        response.setRemarks(result.getRemarks());
        response.setGradedAt(result.getGradedAt());
        response.setReleasedAt(result.getReleasedAt());
        response.setReleased(result.getIsReleased());
        
        if (result.getLecturer() != null) {
            response.setLecturerName(result.getLecturer().getFirstName() + " " + result.getLecturer().getLastName());
        }
        
        return response;
    }
    
    /**
     * Convert Course entity to CourseResponse DTO
     */
    private CourseResponse convertToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .department(course.getDepartment())
                .maxEnrollments(course.getMaxEnrollments())
                .currentEnrollments(course.getCurrentEnrollments())
                .active(course.isActive())
                .build();
    }
}
