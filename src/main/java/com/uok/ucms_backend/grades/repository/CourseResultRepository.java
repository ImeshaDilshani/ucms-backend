package com.uok.ucms_backend.grades.repository;

import com.uok.ucms_backend.grades.entity.CourseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseResultRepository extends JpaRepository<CourseResult, Long> {
    
    /**
     * Find all results for a specific student
     */
    @Query("SELECT cr FROM CourseResult cr WHERE cr.student.id = :studentId AND cr.isReleased = true")
    List<CourseResult> findReleasedResultsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Find all results for a specific student by student number
     */
    @Query("SELECT cr FROM CourseResult cr WHERE cr.studentNumber = :studentNumber AND cr.isReleased = true")
    List<CourseResult> findReleasedResultsByStudentNumber(@Param("studentNumber") String studentNumber);
    
    /**
     * Find all students enrolled in a course for grading (lecturer view)
     */
    @Query("SELECT scr.student FROM com.uok.ucms_backend.enrollments.entity.StudentCourseRegistration scr WHERE scr.course.id = :courseId AND scr.status = 'ACTIVE'")
    List<com.uok.ucms_backend.users.entity.Student> findEnrolledStudentsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Find results by course for lecturer
     */
    @Query("SELECT cr FROM CourseResult cr WHERE cr.course.id = :courseId AND cr.lecturer.id = :lecturerId")
    List<CourseResult> findResultsByCourseIdAndLecturerId(@Param("courseId") Long courseId, @Param("lecturerId") Long lecturerId);
    
    /**
     * Find result by student and course
     */
    Optional<CourseResult> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * Check if lecturer has access to course
     */
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.id = :courseId")
    boolean courseExists(@Param("courseId") Long courseId);
    
    /**
     * Find courses that lecturer can grade (simplified - all active courses for now)
     */
    @Query("SELECT c FROM Course c WHERE c.active = true")
    List<com.uok.ucms_backend.courses.entity.Course> findGradableCourses();
    
    /**
     * Get student registrations for a course
     */
    @Query("SELECT scr FROM com.uok.ucms_backend.enrollments.entity.StudentCourseRegistration scr WHERE scr.course.id = :courseId AND scr.status = 'ACTIVE'")
    List<com.uok.ucms_backend.enrollments.entity.StudentCourseRegistration> findActiveRegistrationsByCourseId(@Param("courseId") Long courseId);
}
