package com.uok.ucms_backend.courses.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {
    
    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Course code is required")
    @Size(max = 16, message = "Course code must not exceed 16 characters")
    private String code;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Credits are required")
    @Positive(message = "Credits must be positive")
    private Integer credits;
    
    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
    
    @NotNull(message = "Max enrollments is required")
    @Positive(message = "Max enrollments must be positive")
    private Integer maxEnrollments;
    
    private List<String> prerequisiteCodes;
}
