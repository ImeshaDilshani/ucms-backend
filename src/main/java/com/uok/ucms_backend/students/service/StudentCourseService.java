package com.uok.ucms_backend.students.service;

import com.uok.ucms_backend.courses.dto.CourseResponse;
import com.uok.ucms_backend.courses.service.CourseService;
import com.uok.ucms_backend.students.dto.StudentCourseViewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentCourseService {
    
    private final CourseService courseService;
    
    /**
     * Convert CourseResponse to StudentCourseViewResponse with additional logic
     */
    public StudentCourseViewResponse convertToStudentView(CourseResponse courseResponse) {
        StudentCourseViewResponse studentView = new StudentCourseViewResponse();
        studentView.setId(courseResponse.getId());
        studentView.setCode(courseResponse.getCode());
        studentView.setName(courseResponse.getName());
        studentView.setDescription(courseResponse.getDescription());
        studentView.setCredits(courseResponse.getCredits());
        studentView.setDepartment(courseResponse.getDepartment());
        studentView.setMaxEnrollments(courseResponse.getMaxEnrollments());
        studentView.setCurrentEnrollments(courseResponse.getCurrentEnrollments());
        
        // Calculate availability based on enrollment capacity
        boolean isAvailable = courseResponse.getCurrentEnrollments() < courseResponse.getMaxEnrollments();
        studentView.setIsAvailable(isAvailable);
        
        // Convert prerequisites
        Set<StudentCourseViewResponse.CoursePrerequisiteInfo> prerequisites = 
            courseResponse.getPrerequisites().stream()
                .map(prereq -> new StudentCourseViewResponse.CoursePrerequisiteInfo(
                    prereq.getId(),
                    prereq.getCode(),
                    prereq.getName(),
                    prereq.getCredits()
                ))
                .collect(Collectors.toSet());
        studentView.setPrerequisites(prerequisites);
        
        return studentView;
    }
    
    /**
     * Convert list of CourseResponse to StudentCourseViewResponse
     */
    public List<StudentCourseViewResponse> convertToStudentViews(List<CourseResponse> courseResponses) {
        return courseResponses.stream()
            .map(this::convertToStudentView)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert page of CourseResponse to page of StudentCourseViewResponse
     */
    public Page<StudentCourseViewResponse> convertToStudentViews(Page<CourseResponse> coursePage) {
        List<StudentCourseViewResponse> studentViews = coursePage.getContent().stream()
            .map(this::convertToStudentView)
            .collect(Collectors.toList());
        
        return new PageImpl<>(studentViews, coursePage.getPageable(), coursePage.getTotalElements());
    }
    
    /**
     * Get available courses for enrollment (courses that still have capacity)
     */
    public List<StudentCourseViewResponse> getAvailableCoursesForEnrollment() {
        log.info("Fetching courses available for enrollment");
        
        List<CourseResponse> allCourses = courseService.getAllAvailableCoursesForStudents();
        
        return allCourses.stream()
            .map(this::convertToStudentView)
            .filter(StudentCourseViewResponse::getIsAvailable) // Only courses with capacity
            .collect(Collectors.toList());
    }
    
    /**
     * Get courses by department that are available for enrollment
     */
    public List<StudentCourseViewResponse> getAvailableCoursesForEnrollmentByDepartment(String department) {
        log.info("Fetching courses available for enrollment by department: {}", department);
        
        List<CourseResponse> courses = courseService.getCoursesByDepartmentForStudents(department);
        
        return courses.stream()
            .map(this::convertToStudentView)
            .filter(StudentCourseViewResponse::getIsAvailable) // Only courses with capacity
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a student can enroll in a course (has capacity and prerequisites met)
     * Note: This is a basic check, actual prerequisite validation would require student's completed courses
     */
    public boolean canEnrollInCourse(Long courseId) {
        log.info("Checking if course {} is available for enrollment", courseId);
        
        try {
            CourseResponse course = courseService.getCourseForStudent(courseId);
            StudentCourseViewResponse studentView = convertToStudentView(course);
            
            // Basic availability check
            return studentView.getIsAvailable();
        } catch (Exception e) {
            log.warn("Could not check enrollment availability for course {}: {}", courseId, e.getMessage());
            return false;
        }
    }
}
