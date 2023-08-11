package com.gleb.dto.message;

import lombok.Data;

@Data
public class MessageReceiveDto {
    private String sender;
    private String content;
}
