package com.gleb.data;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


@Table("comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Comment {

    @Id
    private Integer id;

    @NotBlank
    private String content;

    private Integer postId;


    private String authorName;


    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;


    private String lastModifiedBy;

    private Integer commentIdForPost;

}

