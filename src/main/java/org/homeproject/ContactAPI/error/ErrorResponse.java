package org.homeproject.ContactAPI.error;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}
