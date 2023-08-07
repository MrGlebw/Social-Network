package com.gleb.web;

import com.gleb.exceptions.SubscriptionAlreadyExistsException;
import com.gleb.exceptions.SubscriptionNotFoundException;
import com.gleb.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class CurrentUserSubscriptionController {

    private final SubscriptionFacade subscriptionFacade;


    @PostMapping("/subscribe/{followedUsername}")
    public Mono<ResponseEntity<String>> subscribe(@PathVariable String followedUsername) {
        return subscriptionFacade.subscribe(followedUsername)
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Subscribed"))
                .onErrorResume(UsernameNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage())))
                .onErrorResume(SubscriptionAlreadyExistsException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage())));
    }

    @DeleteMapping("/unsubscribe/{followedUsername}")
    public Mono<ResponseEntity<String>> unsubscribe(@PathVariable String followedUsername) {
        return subscriptionFacade.unsubscribe(followedUsername)
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Unsubscribed"))
                .onErrorResume(SubscriptionNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage())));
    }
}
