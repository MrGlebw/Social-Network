package com.gleb.repo;

import com.gleb.data.Post.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepo extends ReactiveCrudRepository<Post, Long>{
    Flux<Post> findByUserId(Long id);
    Mono<Void> deletePostById(Long id);
}
