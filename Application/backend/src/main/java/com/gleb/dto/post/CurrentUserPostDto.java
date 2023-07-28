package com.gleb.dto.post;

import com.gleb.data.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserPostDto {
    private String title;
    private String content;

    private Status status = Status.DRAFT;


    private LocalDateTime createdDate;


    private LocalDateTime lastModifiedDate;


    private Integer postIdForUser;


    private LocalDateTime publishedDate;

    private LocalDateTime disapprovedDate;
}
