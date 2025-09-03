package com.uok.ucms_backend.grades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledStudentResponse {
    
    private Long studentId;
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String program;
    private Integer year;
    private boolean hasGrade;
    private CourseResultResponse existingResult;
}
