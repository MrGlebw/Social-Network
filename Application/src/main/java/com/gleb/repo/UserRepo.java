package com.gleb.repo;

import com.gleb.data.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepo extends R2dbcRepository<User, Integer> {
    Mono<User> findByUsername(String username);

    @Query("SELECT u.* FROM users u LEFT JOIN refresh_tokens rt ON u.refresh_token_id = rt.id WHERE rt.refresh_token = :refreshToken")
    Mono<User> findByRefreshToken(String refreshToken);


}
