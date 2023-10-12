package com.gleb.facade;


import com.gleb.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionFacade {

    private final SubscriptionService subscriptionService;

    public Mono<Void> subscribe (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    if (followedUsername.equals(followerUsername)) {
                        return Mono.error(new Exception("You can't subscribe to yourself"));
                    }
        return subscriptionService.subscribe(followerUsername, followedUsername);
                });
    }

    public Mono <Void> unsubscribe (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.unsubscribe(followerUsername, followedUsername);
                });
    }

    public Mono <Void> accept (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.accept(followerUsername, followedUsername);
                });
    }

    public Mono <Void> reject (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.reject(followerUsername, followedUsername);
                });
    }


    public Flux<String> showFollowersByUsername (String username , Pageable pageable ) {
                    return subscriptionService.getFollowers(username, pageable)
                            .collectList().flatMapMany(Flux::fromIterable);
                }


    public Flux<String> showFollowings (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followerUsername = authentication.getName();
                    return subscriptionService.getFollowedId(followerUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable);
                });
    }

    public Flux<String> showFollowers (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.getFollowers(followedUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable);
                });
    }

    public Flux<String> showFollowingsByUsername (String username , Pageable pageable) {
        return subscriptionService.getFollowedId(username, pageable)
                .collectList().flatMapMany(Flux::fromIterable);
    }

    public Flux<String> showRequestedToFollowUsers (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.getRequestedToFollowUsers(followedUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable);
                });
    }

    public Flux<String> showMyRequests (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followerUsername = authentication.getName();
                    return subscriptionService. getRequests(followerUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable);
                });
    }




}