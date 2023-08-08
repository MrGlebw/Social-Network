package com.gleb.service;

import com.gleb.data.subscription.Status;
import com.gleb.data.subscription.Subscription;
import com.gleb.exceptions.SubscriptionAlreadyExistsException;
import com.gleb.exceptions.SubscriptionNotFoundException;
import com.gleb.repo.SubscriptionRepo;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final UserService userService;
    private final SubscriptionRepo subscriptionRepo;

    public Mono<Void> subscribe(String followedUsername, String followerUsername) {
        return userService.existsByUsername(followedUsername)
                .flatMap(exists -> {
                    return userService.findUserByUsername(followedUsername)
                            .flatMap(followedUser -> userService.findUserByUsername(followerUsername)
                                    .flatMap(followerUser ->
                                            subscriptionRepo.findByFollowedUserIdAndFollowerId(
                                                    followedUser.getId(), followerUser.getId())
                                            .flatMap(existingSubscription -> {
                                                return Mono.error(new SubscriptionAlreadyExistsException(followedUsername));
                                            })
                                            .switchIfEmpty(subscriptionRepo.save(Subscription.builder()
                                                            .followedUserId(followedUser.getId())
                                                            .followerId(followerUser.getId())
                                                            .requestDate(LocalDateTime.now())
                                                            .build())
                                                    .doOnSuccess(subscription -> log.info("IN subscribe - subscription: {} created", subscription))
                                                    .then())
                                    ));
                }).onErrorResume(e -> {
                            if (e instanceof UsernameNotFoundException) {
                                return Mono.error(new UsernameNotFoundException("User: " + followedUsername + " not found"));
                            } else {
                                return Mono.error(e);
                            }
                        }
                ).then();

    }

    public Mono<Void> unsubscribe(String followedUsername, String followerUsername) {
        return userService.existsByUsername(followedUsername)
                .flatMap(exists -> {
                    return userService.findUserByUsername(followedUsername)
                            .flatMap(followedUser -> userService.findUserByUsername(followerUsername)
                                    .flatMap(followerUser -> subscriptionRepo.findByFollowedUserIdAndFollowerId(
                                                    followedUser.getId(), followerUser.getId())
                                            .flatMap(existingSubscription -> {
                                                        return subscriptionRepo.deleteByFollowedUserIdAndFollowerId(followedUser.getId(), followerUser.getId())
                                                                .doOnSuccess(subscription -> log.info("IN unsubscribe - subscription: {} deleted", subscription));
                                                    }
                                            )
                                    ));
                });
    }

    public Mono<Void> accept(String followerUsername, String followedUsername) {
        return userService.findUserByUsername(followedUsername)
                .flatMap(followedUser -> userService.findUserByUsername(followerUsername)
                        .flatMap(followerUser -> subscriptionRepo.findByFollowedUserIdAndFollowerId(
                                        followedUser.getId(), followerUser.getId())
                                .flatMap(existingSubscription -> {
                                    return subscriptionRepo.setStatusAccepted( Status.ACCEPTED, LocalDateTime.now(), followedUser.getId(), followerUser.getId())
                                            .doOnSuccess(subscription -> log.info("IN accept - subscription: {} updated", subscription));
                                }).onErrorResume(e -> {
                                    if (e instanceof SubscriptionNotFoundException) {
                                        return Mono.error(new SubscriptionNotFoundException(followedUsername));
                                    } else {
                                        return Mono.error(e);
                                    }
                                }

                        )
                ));
    }

    public Mono<Void> reject (String followerUsername, String followedUsername) {
        return userService.findUserByUsername(followedUsername)
                .flatMap(followedUser -> userService.findUserByUsername(followerUsername)
                        .flatMap(followerUser -> subscriptionRepo.findByFollowedUserIdAndFollowerId(
                                        followedUser.getId(), followerUser.getId())
                                .flatMap(existingSubscription -> {
                                    return subscriptionRepo.setStatusRejected( Status.REJECTED, LocalDateTime.now(), followedUser.getId(), followerUser.getId())
                                            .doOnSuccess(subscription -> log.info("IN reject - subscription: {} updated", subscription));
                                }).onErrorResume(e -> {
                                            if (e instanceof SubscriptionNotFoundException) {
                                                return Mono.error(new SubscriptionNotFoundException(followedUsername));
                                            } else {
                                                return Mono.error(e);
                                            }
                                        }

                                )
                        ));
    }

    public Flux<Integer> getFollowersId(String followedUsername, Pageable pageable) {
        return userService.findUserByUsername(followedUsername)
                .flatMapMany(followedUser -> subscriptionRepo.getAllFollowersID (followedUser.getId(), pageable));

    }

    public Flux<Integer> getFollowedId(String followerUsername , Pageable pageable) {
        return userService.findUserByUsername(followerUsername)
                .flatMapMany(followerUser -> subscriptionRepo.getAllFollowedUsersID (followerUser.getId(), pageable));

    }

    public Flux<Integer>  getRequestedToFollowUsers(String followedUsername , Pageable pageable) {
        return userService.findUserByUsername(followedUsername)
                .flatMapMany(followedUser -> subscriptionRepo.getAllRequestedToFollowUsers (followedUser.getId(), pageable));

    }

    public Flux<Integer>  getRequests (String followerUsername, Pageable pageable) {
        return userService.findUserByUsername(followerUsername)
                .flatMapMany(followerUser -> subscriptionRepo.getAllRequests (followerUser.getId(), pageable));

    }

}




