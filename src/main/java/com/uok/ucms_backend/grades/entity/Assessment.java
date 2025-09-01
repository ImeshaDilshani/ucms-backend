package com.uok.ucms_backend.grades.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import com.uok.ucms_backend.courses.entity.Offering;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assessment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;
    
    @Column(nullable = false, length = 64)
    private String name;
    
    @Column(name = "weight_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPercent;
    
    @Column(name = "max_score", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxScore;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
}
