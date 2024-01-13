package com.gleb.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageShowDto {
    private String sender;
    private String content;
    @NotNull
    private LocalDateTime sentAt;
}
