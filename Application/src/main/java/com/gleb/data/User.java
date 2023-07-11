package com.gleb.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Table("users")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class User {
        @Id
        private Integer id;

        private String username;
        @Column("first_name")
        private String firstName;
        @Column("last_name")
        private String lastName;
        @JsonIgnore
        private String password;
        @Email
        private String email;
        private LocalDate birthdate;
        private LocalDateTime createdAt;

        @Builder.Default()
        private boolean active = true;


    private Set<RoleName> roles;

}


