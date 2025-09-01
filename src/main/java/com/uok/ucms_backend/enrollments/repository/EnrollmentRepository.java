package com.uok.ucms_backend.enrollments.repository;

import com.uok.ucms_backend.enrollments.entity.Enrollment;
import com.uok.ucms_backend.enrollments.entity.Enrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status != 'DROPPED'")
    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.offering.id = :offeringId")
    List<Enrollment> findByOfferingId(@Param("offeringId") Long offeringId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.offering.id = :offeringId AND e.status = :status ORDER BY e.enrolledAt")
    List<Enrollment> findByOfferingIdAndStatus(@Param("offeringId") Long offeringId, 
                                               @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.offering.id = :offeringId AND e.status = 'ENROLLED'")
    Long countEnrolledByOfferingId(@Param("offeringId") Long offeringId);
    
    Optional<Enrollment> findByOfferingIdAndStudentId(Long offeringId, Long studentId);
    
    boolean existsByOfferingIdAndStudentIdAndStatusNot(Long offeringId, Long studentId, EnrollmentStatus status);
}
