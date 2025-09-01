package com.uok.ucms_backend.grades.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import com.uok.ucms_backend.users.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assessment_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal score;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt = LocalDateTime.now();
    
    @Column(name = "is_locked")
    private Boolean isLocked = false;
}
