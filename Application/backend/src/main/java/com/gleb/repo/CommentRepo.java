package com.gleb.repo;

import com.gleb.data.Comment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepo extends R2dbcRepository<Comment, Integer> {

    @Query("SELECT * FROM comments WHERE post_id = :postId")
    Flux<Comment> findByPost(Integer postId);

    @Query("SELECT * FROM comments WHERE author_name = :authorName")
    Flux<Comment> findByAuthorName(String authorName);

    @Query("SELECT * FROM comments WHERE post_id = :postId")
    Flux <Comment> findAllByPostId(Integer postId);

    @Query("SELECT * FROM comments WHERE author_name = :authorName AND comment_id_for_post = :commentIdForPost AND post_id = :postId")
    Mono <Comment> findByAuthorName(String authorName , Integer commentIdForPost, Integer postId);

    @Query("SELECT * FROM comments WHERE author_name = :authorName AND post_id = :postId")
    Flux <Comment> findAllByPostIdAndAuthorName(Integer postId, String authorName);

    @Query("DELETE FROM comments WHERE  comment_id_for_post = :commentIdForPost AND post_id = :postId")
    Mono <Void> deleteComment (Integer commentIdForPost, Integer postId);


}
