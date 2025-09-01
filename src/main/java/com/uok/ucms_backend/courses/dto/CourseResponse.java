package com.uok.ucms_backend.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private String department;
    private Integer maxEnrollments;
    private Integer currentEnrollments;
    private Boolean isActive;
    private Set<CoursePrerequisiteResponse> prerequisites;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoursePrerequisiteResponse {
        private Long id;
        private String code;
        private String name;
        private Integer credits;
    }
}
