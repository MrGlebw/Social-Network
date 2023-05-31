package com.gleb.data.User;


import com.gleb.data.Post.Post;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;


@Table(name = "users")
@Getter
@Setter
@Data
public class User {

    @Id
    private Long id;

    @Column
    private String username;

    @Column
    private String firstName;
    @Column
    private String surname;

    @Column
    private LocalDate birthDate;
    @Column
    private String email;
    @Column
    private String password;

    private Set<RoleName> roles;
    @OneToMany(mappedBy = "user")
    private Set<Post> posts;

    private Boolean isActive;
    private Boolean isBanned;
    private Boolean isPrivate;
    private Boolean isDeleted;
    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        isActive = false;
        isDeleted = false;
        isBanned = false;
        isPrivate = false;
        LocalDateTime now = LocalDateTime.now();
        created = now;
        updated = now;
    }

    @PreUpdate
    public void preUpdate() {
        updated = LocalDateTime.now();
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(firstName, user.firstName) && Objects.equals(surname, user.surname) && Objects.equals(birthDate, user.birthDate) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(roles, user.roles) && Objects.equals(isActive, user.isActive) && Objects.equals(isBanned, user.isBanned) && Objects.equals(isPrivate, user.isPrivate) && Objects.equals(isDeleted, user.isDeleted) && Objects.equals(created, user.created) && Objects.equals(updated, user.updated);
    }
}


