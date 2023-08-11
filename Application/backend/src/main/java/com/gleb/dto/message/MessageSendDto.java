package com.gleb.dto.message;

import lombok.Data;

@Data
public class MessageSendDto {
    private String recipient;
    private String content;
}
