package com.gleb.repo;

import com.gleb.data.TextMessage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageRepo extends R2dbcRepository<TextMessage, Integer> {

    @Query("SELECT * FROM messages WHERE sender = :user1 AND recipient = :user2 OR sender = :user2 AND recipient = :user1")
    Flux<TextMessage> findAllByTwoUsernames(String user1, String user2);


    @Query("SELECT content FROM messages WHERE sender = :username OR recipient = :username")
    Flux<String> findByUsername(String username);
}
