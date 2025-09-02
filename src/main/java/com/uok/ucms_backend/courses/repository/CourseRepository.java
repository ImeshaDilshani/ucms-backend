package com.uok.ucms_backend.courses.repository;

import com.uok.ucms_backend.courses.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    
    // Student-specific methods - only active courses
    
    /**
     * Find all active courses
     */
    List<Course> findByActiveTrue();
    
    /**
     * Find active course by ID
     */
    Optional<Course> findByIdAndActiveTrue(Long id);
    
    /**
     * Find active course by code
     */
    Optional<Course> findByCodeAndActiveTrue(String code);
    
    /**
     * Find active courses by department
     */
    List<Course> findByDepartmentAndActiveTrueOrderByCode(String department);
    
    /**
     * Find active courses by credits
     */
    List<Course> findByCreditsAndActiveTrueOrderByCode(Integer credits);
    
    /**
     * Find active courses with filters for students
     */
    @Query("SELECT c FROM Course c WHERE c.active = true AND " +
           "(:code IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:department IS NULL OR LOWER(c.department) LIKE LOWER(CONCAT('%', :department, '%')))")
    Page<Course> findActiveCoursesWithFilters(@Param("code") String code, 
                                             @Param("name") String name,
                                             @Param("department") String department,
                                             Pageable pageable);
    
    /**
     * Search active courses by keyword in name and description
     */
    @Query("SELECT c FROM Course c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchActiveCoursesbyKeyword(@Param("keyword") String keyword, 
                                             Pageable pageable);
}
