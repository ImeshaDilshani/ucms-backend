package com.uok.ucms_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerRegistrationRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username can only contain letters, numbers, dots and underscores")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @NotBlank(message = "Staff number is required")
    @Size(max = 32, message = "Staff number must not exceed 32 characters")
    private String staffNo;
    
    @NotBlank(message = "First name is required")
    @Size(max = 64, message = "First name must not exceed 64 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 64, message = "Last name must not exceed 64 characters")
    private String lastName;
    
    @NotBlank(message = "Department is required")
    @Size(max = 64, message = "Department must not exceed 64 characters")
    private String department;
    
    @NotBlank(message = "Lecturer secret key is required")
    private String lecturerSecretKey;
}
