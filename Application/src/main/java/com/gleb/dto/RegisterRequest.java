package com.gleb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gleb.data.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {


    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private Set<RoleName> roles;



}
