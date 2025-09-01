package com.uok.ucms_backend.courses.repository;

import com.uok.ucms_backend.courses.entity.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    
    @Query("SELECT o FROM Offering o WHERE o.semester.isCurrent = true")
    List<Offering> findCurrentSemesterOfferings();
    
    @Query("SELECT o FROM Offering o WHERE o.semester.id = :semesterId")
    List<Offering> findBySemesterId(@Param("semesterId") Long semesterId);
    
    @Query("SELECT o FROM Offering o WHERE o.lecturer.id = :lecturerId")
    List<Offering> findByLecturerId(@Param("lecturerId") Long lecturerId);
    
    @Query("SELECT o FROM Offering o WHERE " +
           "o.semester.isCurrent = true AND " +
           "(:courseCode IS NULL OR LOWER(o.course.code) LIKE LOWER(CONCAT('%', :courseCode, '%'))) AND " +
           "(:courseTitle IS NULL OR LOWER(o.course.title) LIKE LOWER(CONCAT('%', :courseTitle, '%'))) AND " +
           "(:lecturerName IS NULL OR LOWER(o.lecturer.username) LIKE LOWER(CONCAT('%', :lecturerName, '%')))")
    Page<Offering> findCurrentOfferingsWithFilters(@Param("courseCode") String courseCode,
                                                   @Param("courseTitle") String courseTitle,
                                                   @Param("lecturerName") String lecturerName,
                                                   Pageable pageable);
    
    Optional<Offering> findByCourseIdAndSemesterIdAndSection(Long courseId, Long semesterId, String section);
}
