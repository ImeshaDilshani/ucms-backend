package com.uok.ucms_backend.enrollments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRegistrationResponse {
    
    private Long id;
    private String studentNumber;
    private String courseCode;
    private String courseTitle;
    private String courseName;
    private Integer credits;
    private String department;
    private String courseDescription;
    private LocalDateTime registeredAt;
    private String status;
}
