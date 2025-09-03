package com.uok.ucms_backend.grades.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private String department;
    private Integer maxEnrollments;
    private int currentEnrollments;
    private boolean active;
}
