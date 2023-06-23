package com.gleb.data;

import lombok.*;
import org.springframework.data.annotation.Id;


@Data
@NoArgsConstructor
public class Post {
    @Id
    private Long id;
    private String text;
    private Long userId;
}