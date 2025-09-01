package com.uok.ucms_backend.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Student extends User {
    
    @Column(name = "index_no", unique = true, length = 32)
    private String indexNo;
    
    @Column(name = "first_name", length = 64)
    private String firstName;
    
    @Column(name = "last_name", length = 64)
    private String lastName;
    
    @Column(length = 64)
    private String program;
    
    @Column(name = "\"year\"")
    private Integer year;
}
