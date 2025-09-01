package com.uok.ucms_backend.users.repository;

import com.uok.ucms_backend.users.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    
    Optional<Lecturer> findByStaffNo(String staffNo);
    
    boolean existsByStaffNo(String staffNo);
}
