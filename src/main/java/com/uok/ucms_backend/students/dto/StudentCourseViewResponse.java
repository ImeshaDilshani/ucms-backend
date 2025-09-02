package com.uok.ucms_backend.students.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseViewResponse {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private String department;
    private Integer maxEnrollments;
    private Integer currentEnrollments;
    private Boolean isAvailable; // Based on current enrollments vs max
    private Set<CoursePrerequisiteInfo> prerequisites;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoursePrerequisiteInfo {
        private Long id;
        private String code;
        private String name;
        private Integer credits;
    }
}
