package com.gleb.repo;

import com.gleb.data.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Scanner;

public interface PostRepo extends R2dbcRepository<Post, String> {

    Flux<Post> findByTitleContains(String q, Pageable pageable);

    Mono<Long> countByTitleContains(String q);



}