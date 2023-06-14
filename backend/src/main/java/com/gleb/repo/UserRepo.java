package com.gleb.repo;

import com.gleb.data.user.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepo extends R2dbcRepository <User, String> {
    Mono<Optional<User>> findByUsername(String username);

    Mono<Optional<User>> findByEmail(String email);

    Flux<User> findAllByBirthDate(LocalDate birthDate);
    Flux <User> findAllByIsActive(Boolean isActive);
    Mono<Boolean> existsByUsername (String username);
    Mono <Boolean> existsByEmail (String email);








}

