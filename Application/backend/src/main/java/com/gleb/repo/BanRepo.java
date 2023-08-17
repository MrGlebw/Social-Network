package com.gleb.repo;

import com.gleb.data.Ban;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



public interface BanRepo extends R2dbcRepository <Ban, Long> {

    @Query("SELECT  * FROM bans WHERE from_user = :fromUser AND to_user = :toUser")
    Mono<Ban> findByFromUsernameAndToUsername(String fromUser, String toUser);

    @Query("INSERT INTO bans (from_user, to_user, banned_at) VALUES (:fromUser, :toUser, NOW())")
    Mono <Ban> saveByFromUsernameAndToUsername(String fromUser, String toUser);

    @Query("DELETE FROM bans WHERE from_user = :fromUser AND to_user = :toUser")
    Mono<Void> deleteByFromUsernameAndToUsername(String fromUser, String toUser);

    @Query("SELECT to_user FROM bans WHERE from_user = :fromUser")
    Flux <String> findByFromUsername(String fromUser, Pageable pageable);

    @Query("SELECT from_user FROM bans WHERE to_user = :toUser")
    Flux <String> findByToUsername(String toUser, Pageable pageable);

    @Query("SELECT EXISTS(SELECT * FROM bans WHERE from_user = :fromUser AND to_user = :toUser)")
    Mono <Boolean> existsByFromUsernameAndToUsername(String fromUser, String toUser);
}
