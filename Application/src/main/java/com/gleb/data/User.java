package com.gleb.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("users")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
     @Builder
    public class User {
        @Id
        private Long id;

        private String username;
        @JsonIgnore
        private String password;
        @Email
        private String email;
        private LocalDate birthdate;

        @Builder.Default()
        private boolean active = true;

        @Builder.Default()
        private List<String> roles = new ArrayList<>();
    }