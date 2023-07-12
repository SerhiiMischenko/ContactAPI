package org.homeproject.ContactAPI.error;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;


    public void statusNotFound(Long id, String message, String path) {
        this.setStatus(HttpStatus.NOT_FOUND.value());
        this.setMessage(message);
        this.setTimestamp(LocalDateTime.now());
        this.setPath(path + id);

    }
    public void statusOk(Long id, String message, String path) {
        this.setStatus(HttpStatus.OK.value());
        this.setMessage(message);
        this.setTimestamp(LocalDateTime.now());
        this.setPath(path + id);

    }
    public void statusNotValid(String message, String path) {
        this.setStatus(HttpStatus.BAD_REQUEST.value());
        this.setMessage(message);
        this.setTimestamp(LocalDateTime.now());
        this.setPath(path);
    }

    public void statusNotAuthorized(String message, String path) {
        this.setStatus(HttpStatus.FORBIDDEN.value());
        this.setMessage(message);
        this.setTimestamp(LocalDateTime.now());
        this.setPath(path);
    }
}
