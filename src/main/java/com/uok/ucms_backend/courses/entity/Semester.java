package com.uok.ucms_backend.courses.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Semester extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 32)
    private String name;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "add_drop_start", nullable = false)
    private LocalDate addDropStart;
    
    @Column(name = "add_drop_end", nullable = false)
    private LocalDate addDropEnd;
    
    @Column(name = "is_current")
    private Boolean isCurrent = false;
}
