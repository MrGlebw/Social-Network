package com.gleb.data;

import lombok.*;


@Data
@NoArgsConstructor
public class Post {
    private Long id;
    private String text;
    private Long userId;
}