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

    @Query("SELECT * FROM posts WHERE title ILIKE '%' || :title || '%'")
    Flux<Post> findByTitleContains(String title, Pageable pageable);

    @Query("SELECT * FROM posts WHERE author_name = :author_name")
    Flux<Post> allPostsByAuthorName(String authorName);


    @Query("DELETE FROM posts WHERE post_id_for_user = :postIdForUser AND author_name = :authorName")
    Mono <Void> deleteByPostIdForUserAndAuthorName (Integer postIdForUser , String authorName);



}





