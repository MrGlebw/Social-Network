package com.gleb.data.post;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Table("posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Post implements Serializable {

    @Id
    private Integer id;

    @Column("author_name")
    private String authorName;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Builder.Default
    private Status status = Status.DRAFT;

    @CreatedDate
    @Column("created_date")
    private LocalDateTime createdDate;


    @LastModifiedDate
    @Column("last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column("post_id_for_user")
    private Integer postIdForUser;


    private LocalDateTime publishedDate;

    private LocalDateTime disapprovedDate;

    private Integer commentsCount;


}

