package com.uok.ucms_backend.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lecturers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Lecturer extends User {
    
    @Column(name = "staff_no", unique = true, length = 32)
    private String staffNo;
    
    @Column(name = "first_name", length = 64)
    private String firstName;
    
    @Column(name = "last_name", length = 64)
    private String lastName;
    
    @Column(length = 64)
    private String department;
}
