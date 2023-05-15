package com.gleb.repo;

import com.gleb.data.User.User;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepo extends R2dbcRepository<User, Long> {
    Mono<Optional<User>> findByUsername(String username);
}
