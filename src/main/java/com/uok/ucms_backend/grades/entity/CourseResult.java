package com.uok.ucms_backend.grades.entity;

import com.uok.ucms_backend.common.entity.BaseEntity;
import com.uok.ucms_backend.courses.entity.Course;
import com.uok.ucms_backend.users.entity.Lecturer;
import com.uok.ucms_backend.users.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResult extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;
    
    @Column(name = "student_number", nullable = false, length = 32)
    private String studentNumber;
    
    @Column(name = "course_code", nullable = false, length = 16)
    private String courseCode;
    
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @Column(name = "marks", precision = 5, scale = 2)
    private BigDecimal marks;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 2)
    private Grade grade;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt = LocalDateTime.now();
    
    @Column(name = "is_released", nullable = false)
    private Boolean isReleased = false;
    
    @Column(name = "released_at")
    private LocalDateTime releasedAt;
    
    public enum Grade {
        A_PLUS("A+", 85, 100),
        A("A", 80, 84),
        A_MINUS("A-", 75, 79),
        B_PLUS("B+", 70, 74),
        B("B", 65, 69),
        B_MINUS("B-", 60, 64),
        C_PLUS("C+", 55, 59),
        C("C", 50, 54),
        C_MINUS("C-", 45, 49),
        D("D", 40, 44),
        FAIL("F", 0, 39);
        
        private final String displayName;
        private final double minMarks;
        private final double maxMarks;
        
        Grade(String displayName, double minMarks, double maxMarks) {
            this.displayName = displayName;
            this.minMarks = minMarks;
            this.maxMarks = maxMarks;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getMinMarks() {
            return minMarks;
        }
        
        public double getMaxMarks() {
            return maxMarks;
        }
        
        public static Grade fromMarks(double marks) {
            for (Grade grade : Grade.values()) {
                if (marks >= grade.minMarks && marks <= grade.maxMarks) {
                    return grade;
                }
            }
            return FAIL;
        }
    }
}
