package com.uok.ucms_backend.users.repository;

import com.uok.ucms_backend.users.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByIndexNo(String indexNo);
    
    boolean existsByIndexNo(String indexNo);
}
