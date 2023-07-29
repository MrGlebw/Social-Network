package com.gleb.data;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;


@Table("comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private Integer id;

    @NotBlank
    private String content;

    private Integer post;



    private String authorName;


    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;


    private String lastModifiedBy;

    private Integer commentIdForPost;

}

