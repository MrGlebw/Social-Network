package com.gleb.service.user;

import com.gleb.data.user.Roles;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepo userRepo;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByUsername(username)
                .map(user -> {
                    logger.debug("User found in the database: {}", user.getUsername());
                    Set<Roles> roles = user.getRoles();
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority(role.name()))
                            .collect(Collectors.toList());
                    return User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities(authorities)
                            .accountExpired(!user.getIsActive())
                            .credentialsExpired(!user.getIsActive())
                            .disabled(!user.getIsActive())
                            .accountLocked(!user.getIsActive())
                            .build();
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }
}