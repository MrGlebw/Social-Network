package com.gleb.dto.post;

import com.gleb.data.post.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserPostDto {
    private String title;
    private String content;

    @Builder.Default
    private Status status = Status.DRAFT;


    private LocalDateTime createdDate;


    private LocalDateTime lastModifiedDate;


    private Integer postIdForUser;


    private LocalDateTime publishedDate;

    private LocalDateTime disapprovedDate;
}
