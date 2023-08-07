package com.gleb.facade;

import com.gleb.service.SubscriptionService;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionFacade {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    public Mono<Object> subscribe (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
        return subscriptionService.subscribe(followerUsername, followedUsername);
                });
    }

    public Mono <Object> unsubscribe (String followerUsername) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String followedUsername = authentication.getName();
                    return subscriptionService.unsubscribe(followerUsername, followedUsername);
                });
    }
}