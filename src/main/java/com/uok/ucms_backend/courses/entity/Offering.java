package com.uok.ucms_backend.courses.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import com.uok.ucms_backend.users.entity.Lecturer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "offerings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_id", "semester_id", "section"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offering extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
    
    @Column(nullable = false, length = 8)
    private String section;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(length = 32)
    private String room;
    
    @Column(columnDefinition = "TEXT")
    private String schedule; // JSON string for schedule
}
