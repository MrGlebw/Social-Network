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
    @Query("SELECT followed_user_id FROM subscriptions WHERE follower_id = :followerId AND status = 'ACCEPTED'")
    Flux<Integer> getAllFollowedUsersID(Integer followerId, Pageable pageable);

    @Query("SELECT follower_id FROM subscriptions WHERE followed_user_id = :followedUserId AND status = 'ACCEPTED'")
    Flux <Integer> getAllFollowersID(Integer followedUserId, Pageable pageable);

    @Query("SELECT follower_id FROM subscriptions WHERE followed_user_id = :followedUserId AND status = 'REQUESTED'")
    Flux <Integer> getAllRequestedToFollowUsers(Integer followedUserId, Pageable pageable);

    @Query("SELECT * FROM subscriptions WHERE follower_id = :followerId AND status = 'REQUESTED'")
    Flux <Integer> getAllRequests(Integer followerId, Pageable pageable);

    @Query("SELECT * FROM subscriptions WHERE followed_user_id = :followedUserId AND follower_id = :followerId")
    Mono <Integer> getFollowerOfUser(Integer followedUserId, Integer followerId);

    @Query("SELECT * FROM subscriptions WHERE follower_id = :followerId AND followed_user_id = :followedUserId")
    Mono <Integer> getFollowedUserByUser(Integer followerId , Integer followedUserId);

    @Query("SELECT * FROM subscriptions WHERE followed_user_id = :followedUserId AND follower_id = :followerId")
    Mono<Subscription> findByFollowedUserIdAndFollowerId(Integer followedUserId, Integer followerId);

    @Query("DELETE FROM subscriptions WHERE followed_user_id = :followedUserId AND follower_id = :followerId")
    Mono <Void> deleteByFollowedUserIdAndFollowerId(Integer followedUserId, Integer followerId);

    @Query("UPDATE subscriptions SET status = :status, accept_date = :acceptDate WHERE followed_user_id = :followedUserId AND follower_id = :followerId")
    Mono<Void> setStatusAccepted(Status status, LocalDateTime acceptDate, Integer followedUserId, Integer followerId);

    @Query("UPDATE subscriptions SET status = :status, reject_date = :rejectDate WHERE followed_user_id = :followedUserId AND follower_id = :followerId")
    Mono <Void> setStatusRejected(Status status, LocalDateTime rejectDate, Integer followedUserId, Integer followerId);



}
