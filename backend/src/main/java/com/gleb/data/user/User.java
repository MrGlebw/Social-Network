package com.gleb.data.user;


import com.gleb.data.Post;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Data
public class User {



    private Long id;


    private String username;


    private String firstName;


    private String lastName;


    private LocalDate birthDate;


    private String email;


    private String password;


    private Set<RoleName> roles;


    private Boolean isActive;

    private Boolean isDeleted;


    private Boolean isBanned;


    private Boolean isPrivate;


    private OffsetDateTime created;

    private OffsetDateTime updated;


    private List <Post> posts;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(birthDate, user.birthDate) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(roles, user.roles) && Objects.equals(isActive, user.isActive) && Objects.equals(isDeleted, user.isDeleted) && Objects.equals(isBanned, user.isBanned) && Objects.equals(isPrivate, user.isPrivate) && Objects.equals(created, user.created) && Objects.equals(updated, user.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstName, lastName, birthDate, email, password, roles, isActive, isDeleted, isBanned, isPrivate, created, updated);
    }
}


