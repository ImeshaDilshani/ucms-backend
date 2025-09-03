package com.uok.ucms_backend.enrollments.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import com.uok.ucms_backend.courses.entity.Course;
import com.uok.ucms_backend.users.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_course_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseRegistration extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Column(name = "student_number", nullable = false, length = 32)
    private String studentNumber; // This will be the index_no from student
    
    @Column(name = "course_code", nullable = false, length = 16)
    private String courseCode;
    
    @Column(name = "course_title", nullable = false)
    private String courseTitle;
    
    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RegistrationStatus status = RegistrationStatus.ACTIVE;
    
    public enum RegistrationStatus {
        ACTIVE, DROPPED
    }
}
