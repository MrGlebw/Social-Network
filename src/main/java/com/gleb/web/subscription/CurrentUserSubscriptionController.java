package com.gleb.web.subscription;

import com.gleb.facade.SubscriptionFacade;
import com.gleb.dto.subscription.ShowFollowerDto;
import com.gleb.dto.subscription.ShowFollowingDto;
import com.gleb.exceptions.SubscriptionAlreadyExistsException;
import com.gleb.exceptions.SubscriptionNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Comparator.comparing;

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

    @PatchMapping("/accept/{followerUsername}")
    public Mono<ResponseEntity<String>> accept(@PathVariable String followerUsername) {
        return subscriptionFacade.accept(followerUsername)
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Accepted"))
                .onErrorResume(SubscriptionNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage())));
    }

    @PatchMapping("/reject/{followerUsername}")
    public Mono<ResponseEntity<String>> reject(@PathVariable String followerUsername) {
        return subscriptionFacade.reject(followerUsername)
                .thenReturn(ResponseEntity.status(HttpStatus.OK).body("Rejected"))
                .onErrorResume(SubscriptionNotFoundException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage())));
    }



    @GetMapping("/followings")
    public Mono<ResponseEntity<List<ShowFollowingDto>>> getMyFollowings(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return subscriptionFacade.showFollowings(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }
    @GetMapping("/followers")
    public Mono<ResponseEntity<List<ShowFollowerDto>>> getMyFollowers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        return subscriptionFacade.showFollowers(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }
    @GetMapping("/requestsToFollow")
    public Mono<ResponseEntity<List<ShowFollowerDto>>> getRequestsToFollow(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return subscriptionFacade.showRequestedToFollowUsers(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }

    @GetMapping("/myRequests")
    public Mono<ResponseEntity<List<ShowFollowingDto>>> getMyRequests(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return subscriptionFacade.showMyRequests(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }

}
