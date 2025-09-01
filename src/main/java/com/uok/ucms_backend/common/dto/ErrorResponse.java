package com.uok.ucms_backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private LocalDateTime timestamp;
    
    public ErrorResponse(String title, int status, String detail) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.timestamp = LocalDateTime.now();
        this.type = "about:blank";
    }
}
