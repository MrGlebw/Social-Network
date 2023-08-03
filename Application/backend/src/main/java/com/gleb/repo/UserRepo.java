package com.gleb.repo;

import com.gleb.data.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


@Repository
public interface UserRepo extends R2dbcRepository<User, Integer> {
    @Query("SELECT * FROM users WHERE username = :username")
    Mono<User> findByUsername(String username);



    Flux <User> findByFirstNameAndLastName (String firstName, String lastName);

    @Query("SELECT * FROM users WHERE is_private = false")
    Flux <User> findAllPublicUsers ();


    Mono <Void> deleteByUsername (String username);

    @Query("UPDATE users SET posts_count = :postsCount WHERE username = :username")
    Mono<Void> updatePostsCount(String username, Integer postsCount);



    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    @Query("UPDATE users SET is_private = true WHERE username = :username")
    Mono <Void> makePrivate (String username);
    @Query("UPDATE users SET is_private = false WHERE username = :username")
    Mono <Void> makePublic (String username);
    @Query("UPDATE users SET is_banned = true WHERE username = :username")
    Mono <Void> ban (String username);
    @Query("UPDATE users SET is_banned = false WHERE username = :username")
    Mono <Void> unban (String username);

}
