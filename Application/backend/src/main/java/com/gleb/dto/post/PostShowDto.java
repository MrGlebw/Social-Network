package com.gleb.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostShowDto {
    private Integer id;
    private String  title;
    private String  content;
    private String  authorName;
    private LocalDateTime publishedDate;
}
