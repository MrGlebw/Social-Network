package com.gleb.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserCommentDto {

    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Integer postId;
    private Integer commentIdForPost;


}
