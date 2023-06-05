package com.gleb.dto;

import com.gleb.data.User.User;
import lombok.Data;

@Data
public class PostDTO {
    private String title;
    private String content;
    private User user;

}
