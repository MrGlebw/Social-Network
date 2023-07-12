package com.gleb.repo;

import com.gleb.data.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface PostRepo extends R2dbcRepository<Post, String> {

    Flux<Post> findByTitleContains(String q, Pageable pageable);

    Mono<Long> countByTitleContains(String q);



}