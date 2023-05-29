package com.gleb.repo;

import com.gleb.data.User.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepo extends ReactiveCrudRepository<User, Long> {
    Mono<Optional<User>> findByUsername(String username);
}
