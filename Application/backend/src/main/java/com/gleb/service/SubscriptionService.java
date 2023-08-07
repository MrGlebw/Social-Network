package com.gleb.service;

import com.gleb.data.subscription.Subscription;
import com.gleb.exceptions.SubscriptionAlreadyExistsException;
import com.gleb.exceptions.SubscriptionNotFoundException;
import com.gleb.repo.SubscriptionRepo;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final UserService userService;
    private final SubscriptionRepo subscriptionRepo;

    public Mono<Object> subscribe(String followedUsername, String followerUsername) {
        return userService.existsByUsername(followedUsername)
                .flatMap(exists -> {
                    return userService.findUserByUsername(followedUsername)
                            .flatMap(followedUser -> userService.findUserByUsername(followerUsername)
                                    .flatMap(followerUser -> subscriptionRepo.findByFollowedUserIdAndFollowerId(
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
                );

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
}





