package com.uok.ucms_backend.enrollments.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import com.uok.ucms_backend.courses.entity.Offering;
import com.uok.ucms_backend.users.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"offering_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EnrollmentStatus status;
    
    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();
    
    @Column(name = "dropped_at")
    private LocalDateTime droppedAt;
    
    public enum EnrollmentStatus {
        ENROLLED, WAITLISTED, DROPPED
    }
}
