package com.uok.ucms_backend.enrollments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRegistrationRequest {
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
}
