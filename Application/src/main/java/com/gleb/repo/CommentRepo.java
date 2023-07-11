package com.gleb.repo;

import com.gleb.data.Comment;
import com.gleb.data.PostId;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepo extends R2dbcRepository<Comment, Integer> {


    Flux<Comment> findByPost(PostId id);

    Mono<Long> countByPost(PostId id);

}
