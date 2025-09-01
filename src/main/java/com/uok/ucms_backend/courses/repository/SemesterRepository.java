package com.uok.ucms_backend.courses.repository;

import com.uok.ucms_backend.courses.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    Optional<Semester> findByName(String name);
    
    Optional<Semester> findByIsCurrent(Boolean isCurrent);
    
    boolean existsByName(String name);
}
