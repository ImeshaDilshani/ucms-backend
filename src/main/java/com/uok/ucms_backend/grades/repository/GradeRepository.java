package com.uok.ucms_backend.grades.repository;

import com.uok.ucms_backend.grades.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.assessment.id = :assessmentId")
    List<Grade> findByAssessmentId(@Param("assessmentId") Long assessmentId);
    
    @Query("SELECT g FROM Grade g WHERE g.assessment.offering.id = :offeringId AND g.student.id = :studentId")
    List<Grade> findByOfferingIdAndStudentId(@Param("offeringId") Long offeringId, 
                                            @Param("studentId") Long studentId);
    
    Optional<Grade> findByAssessmentIdAndStudentId(Long assessmentId, Long studentId);
}
