package com.gleb.error;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;



@Getter
@Setter
public class ApiError {
    private int status;
    private String message;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
