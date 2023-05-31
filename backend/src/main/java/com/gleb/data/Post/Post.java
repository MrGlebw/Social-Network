package com.gleb.data.Post;

import com.gleb.data.User.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
@Table(name = "posts")
@Getter
@Setter
@Data
public class Post {
    private Long id;
    private String title;
    private String content;
    private List<String> comments;
    private Long likes;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    private LocalDate created;
    private LocalDate updated;
    private Boolean isDeleted;
    private Boolean isPrivate;
    private Boolean isBanned;
    private Boolean isActive;
    private Boolean isEdited;

    @PrePersist
    public void prePersist() {
        isDeleted = false;
        isPrivate = false;
        isBanned = false;
        isActive = false;
        isEdited = false;
        LocalDate now = LocalDate.now();
        created = now;
        updated = now;
    }

    @PreUpdate
    public void preUpdate() {
        updated = LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(content, post.content) && Objects.equals(comments, post.comments) && Objects.equals(likes, post.likes) && Objects.equals(date, post.date) && Objects.equals(created, post.created) && Objects.equals(updated, post.updated) && Objects.equals(isDeleted, post.isDeleted) && Objects.equals(isPrivate, post.isPrivate) && Objects.equals(isBanned, post.isBanned) && Objects.equals(isActive, post.isActive) && Objects.equals(isEdited, post.isEdited);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, comments, likes, date, created, updated, isDeleted, isPrivate, isBanned, isActive, isEdited);
    }
}
