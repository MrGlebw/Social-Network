package com.gleb.error;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;


    @JsonGetter("status")
    public int getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonGetter("message")
    public String getMessage() {
        return message;
    }

    @JsonSetter("message")
    public void setMessage(String message) {
        this.message = message;
    }
}