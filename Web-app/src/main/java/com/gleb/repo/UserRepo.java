package com.gleb.repo;

import com.gleb.data.User.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepo extends R2dbcRepository <User, Long>{
    Mono<User> findByUsername(String username);
}
