package com.gleb.service;

import com.gleb.data.Roles;
import com.gleb.data.User;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
        // Convert Set<Roles> to a comma-separated string

        return userRepo.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .roles(user.getRoles())
                        .enabled(true)
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build()
        ).doOnSuccess(u -> {
            log.info("IN registerUser - user: {} created", u);
        });
    }


    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByUsername(username)
                .map(user -> (UserDetails) user)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }




}

