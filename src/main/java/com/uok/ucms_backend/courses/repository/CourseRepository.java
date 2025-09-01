package com.uok.ucms_backend.courses.repository;

import com.uok.ucms_backend.courses.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findByCode(String code);
    
    boolean existsByCode(String code);
    
    @Query("SELECT c FROM Course c WHERE " +
           "(:code IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "(:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<Course> findWithFilters(@Param("code") String code, 
                                 @Param("title") String title, 
                                 Pageable pageable);
}
