package com.uok.ucms_backend.enrollments.repository;

import com.uok.ucms_backend.enrollments.entity.StudentCourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRegistrationRepository extends JpaRepository<StudentCourseRegistration, Long> {
    
    /**
     * Find all active registrations for a student by their student number (index_no)
     */
    @Query("SELECT scr FROM StudentCourseRegistration scr WHERE scr.studentNumber = :studentNumber AND scr.status = 'ACTIVE'")
    List<StudentCourseRegistration> findActiveRegistrationsByStudentNumber(@Param("studentNumber") String studentNumber);
    
    /**
     * Find all active registrations for a student by student ID
     */
    @Query("SELECT scr FROM StudentCourseRegistration scr WHERE scr.student.id = :studentId AND scr.status = 'ACTIVE'")
    List<StudentCourseRegistration> findActiveRegistrationsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Find a specific registration by student and course
     */
    Optional<StudentCourseRegistration> findByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, StudentCourseRegistration.RegistrationStatus status);
    
    /**
     * Check if student is already registered for a course
     */
    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, StudentCourseRegistration.RegistrationStatus status);
    
    /**
     * Count active registrations for a course
     */
    @Query("SELECT COUNT(scr) FROM StudentCourseRegistration scr WHERE scr.course.id = :courseId AND scr.status = 'ACTIVE'")
    Long countActiveRegistrationsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Find registration by student number and course code
     */
    @Query("SELECT scr FROM StudentCourseRegistration scr WHERE scr.studentNumber = :studentNumber AND scr.courseCode = :courseCode AND scr.status = 'ACTIVE'")
    Optional<StudentCourseRegistration> findByStudentNumberAndCourseCode(@Param("studentNumber") String studentNumber, @Param("courseCode") String courseCode);
}
