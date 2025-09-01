package com.uok.ucms_backend.courses.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 16)
    private String code;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer credits;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String department;
    
    @Column(nullable = true)
    private Integer maxEnrollments;
    
    @Column(nullable = false)
    private int currentEnrollments = 0;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_prerequisites",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Course> prerequisites = new HashSet<>();
    
    @ManyToMany(mappedBy = "prerequisites", fetch = FetchType.LAZY)
    private Set<Course> dependentCourses = new HashSet<>();
}
