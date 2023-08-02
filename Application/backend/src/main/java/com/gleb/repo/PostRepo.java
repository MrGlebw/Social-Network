package com.gleb.repo;

import com.gleb.data.post.Post;
import com.gleb.data.post.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface PostRepo extends R2dbcRepository<Post, Integer> {

    @Query("SELECT * FROM posts WHERE title ILIKE '%' || :title || '%'")
    Flux<Post> findByTitleContains(String title, Pageable pageable);

    @Query("SELECT * FROM posts WHERE author_name = :author_name")
    Flux<Post> allPostsByAuthorName(String authorName);


    @Query("DELETE FROM posts WHERE post_id_for_user = :postIdForUser AND author_name = :authorName")
    Mono<Boolean> deleteByPostIdForUserAndAuthorName(Integer postIdForUser, String authorName);

    @Query("SELECT * FROM posts WHERE status = :status")
    Flux<Post> findByStatus(Status status, Pageable pageable);

    @Query("SELECT comments_count FROM posts WHERE id = :id  ")
    Mono <Integer> commentsCount (Integer id);



}





