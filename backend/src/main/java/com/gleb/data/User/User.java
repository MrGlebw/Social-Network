package com.gleb.data.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usr")
@Getter
@Setter
@RequiredArgsConstructor
public class User {

    @Id
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    private String firstName;

    private String surname;

    private LocalDate birthDate;

    private String email;
    private String password;
    private Set<RoleName> roles;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    public User toUserRegisterDTO() {
        User user = new User();
        user.setUsername(this.username);
        user.setFirstName(this.firstName);
        user.setSurname(this.surname);
        user.setBirthDate(this.birthDate);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRoles(this.roles);
        return user;
    }
}
