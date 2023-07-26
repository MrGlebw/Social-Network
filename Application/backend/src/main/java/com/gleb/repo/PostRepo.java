package com.gleb.repo;

import com.gleb.data.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface PostRepo extends R2dbcRepository<Post, Integer> {

    Flux<Post> findByTitleContains(String title, Pageable pageable);

    @Query("SELECT * FROM posts WHERE author_name = :author_name")
    Flux<Post> allPostsByAuthorName(String authorName);


}





