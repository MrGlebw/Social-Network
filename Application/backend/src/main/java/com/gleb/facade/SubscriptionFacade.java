package com.gleb.facade;

import com.gleb.dto.subscription.ShowFollowerDto;
import com.gleb.dto.subscription.ShowFollowingDto;
import com.gleb.service.SubscriptionService;
import com.gleb.service.user.UserService;
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
    private final UserService userService;

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

    public Mono <Object> accept (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.accept(followerUsername, followedUsername);
                });
    }

    public Mono <Object> reject (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.reject(followerUsername, followedUsername);
                });
    }
    public Flux<ShowFollowerDto> showFollowers (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.getFollowersId(followedUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable)
                            .flatMap(this::wrapFollower);
                });
    }

    public Flux<ShowFollowerDto> showFollowersByUsername (String username , Pageable pageable ) {
                    return subscriptionService.getFollowersId(username, pageable)
                            .collectList().flatMapMany(Flux::fromIterable)
                            .flatMap(this::wrapFollower);
                }


    public Flux<ShowFollowingDto> showFollowings (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followerUsername = authentication.getName();
                    return subscriptionService.getFollowedId(followerUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable)
                            .flatMap(this::wrapFollowing);
                });
    }

    public Flux<ShowFollowingDto> showFollowingsByUsername (String username , Pageable pageable) {
        return subscriptionService.getFollowedId(username, pageable)
                .collectList().flatMapMany(Flux::fromIterable)
                .flatMap(this::wrapFollowing);
    }

    public Flux<ShowFollowerDto> showRequestedToFollowUsers (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.getRequestedToFollowUsers(followedUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable)
                            .flatMap(this::wrapFollower);
                });
    }

    public Flux<ShowFollowingDto> showMyRequests (Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMapMany(authentication -> {
                    String followerUsername = authentication.getName();
                    return subscriptionService. getRequests(followerUsername, pageable)
                            .collectList().flatMapMany(Flux::fromIterable)
                            .flatMap(this::wrapFollowing);
                });
    }


    private  Mono <ShowFollowerDto> wrapFollower (Integer followerId) {
        return userService.findById(followerId)
                .map(user -> new ShowFollowerDto(user.getUsername()));
    }

    private Mono <ShowFollowingDto> wrapFollowing (Integer followedId) {
        return userService.findById(followedId)
                .map(user -> new ShowFollowingDto(user.getUsername()));
    }

}