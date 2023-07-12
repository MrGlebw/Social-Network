package com.gleb.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("users")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public class User {
        @Id
        private Long id;

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
        private boolean enabled;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
         @Builder.Default()
         private boolean active = true;

         @Builder.Default()
         private List<String> roles = new ArrayList<>();
}


