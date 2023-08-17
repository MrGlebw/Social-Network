package com.gleb.data.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User implements UserDetails {
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

    private Set<Roles> roles = new HashSet<>();


    private Boolean isActive;


    private Boolean isDeleted;


    private Boolean isPrivate;


    private LocalDateTime created;

    private LocalDateTime updated;

    private Boolean enabled;

    private Integer postsCount;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(GrantedAuthority.class::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}


