package com.gleb.data.User;


import com.gleb.data.Post;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("users")
public class User {


    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("roles")
    private Set<RoleName> role;

    @Column("is_active")
    private Boolean isActive;

    @Column("is_deleted")
    private Boolean isDeleted;

    @Column("is_banned")
    private Boolean isBanned;

    @Column("is_private")
    private Boolean isPrivate;

    @Column("created_at")
    private OffsetDateTime created;
    @Column("updated_at")
    private OffsetDateTime updated;

    @Column("posts")
    private List <Post> posts;

    @ToString.Include(name = "password")
    private String masterPassword(){
        return "**************";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(birthDate, user.birthDate) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(role, user.role) && Objects.equals(isActive, user.isActive) && Objects.equals(isDeleted, user.isDeleted) && Objects.equals(isBanned, user.isBanned) && Objects.equals(isPrivate, user.isPrivate) && Objects.equals(created, user.created) && Objects.equals(updated, user.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstName, lastName, birthDate, email, password, role, isActive, isDeleted, isBanned, isPrivate, created, updated);
    }
}


