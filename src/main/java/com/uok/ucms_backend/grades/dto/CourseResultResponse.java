package com.uok.ucms_backend.grades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResultResponse {
    
    private Long id;
    private String studentNumber;
    private String studentName;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private BigDecimal marks;
    private String grade;
    private String gradeDisplay;
    private String remarks;
    private LocalDateTime gradedAt;
    private LocalDateTime releasedAt;
    private boolean isReleased;
    private String lecturerName;
}
