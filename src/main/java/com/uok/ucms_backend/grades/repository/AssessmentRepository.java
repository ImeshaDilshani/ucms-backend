package com.uok.ucms_backend.grades.repository;

import com.uok.ucms_backend.grades.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    @Query("SELECT a FROM Assessment a WHERE a.offering.id = :offeringId")
    List<Assessment> findByOfferingId(@Param("offeringId") Long offeringId);
}
