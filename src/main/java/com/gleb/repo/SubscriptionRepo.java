package com.gleb.repo;

import com.gleb.data.subscription.Status;
import com.gleb.data.subscription.Subscription;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SubscriptionRepo extends R2dbcRepository<Subscription, Integer>  {
    @Query("SELECT followed FROM subscriptions WHERE follower  = :follower AND status = 'ACCEPTED'")
    Flux<String> getAllFollowedUsers(String follower, Pageable pageable);

    @Query("SELECT follower  FROM subscriptions WHERE followed = :followed AND status = 'ACCEPTED'")
    Flux <String> getAllFollowers(String followed, Pageable pageable);

    @Query("SELECT follower FROM subscriptions WHERE followed = :followed AND status = 'REQUESTED'")
    Flux <String> getAllRequestedToFollowUsers(String followed, Pageable pageable);

    @Query("SELECT followed FROM subscriptions WHERE follower = :follower AND status = 'REQUESTED'")
    Flux <String> getAllRequests(String follower, Pageable pageable);


    @Query("SELECT * FROM subscriptions WHERE followed = :followed AND follower = :follower")
    Mono <Subscription> findByFollowedUserAndFollower(String followed, String follower);

    @Query("DELETE FROM subscriptions WHERE followed = :followed AND follower = :follower")
    Mono <Void> deleteByFollowedUserAndFollower(String followed, String follower);

    @Query("UPDATE subscriptions SET status = :status, accept_date = :acceptDate WHERE followed = :followed AND follower = :follower")
    Mono<Void> setStatusAccepted(Status status, LocalDateTime acceptDate, String followed, String follower);

    @Query("UPDATE subscriptions SET status = :status, reject_date = :rejectDate WHERE followed = :followed AND follower = :follower")
    Mono <Void> setStatusRejected(Status status, LocalDateTime rejectDate, String followed, String follower);



}
