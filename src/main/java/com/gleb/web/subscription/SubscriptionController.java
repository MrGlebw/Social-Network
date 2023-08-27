package com.gleb.web.subscription;

import com.gleb.facade.SubscriptionFacade;
import com.gleb.dto.subscription.ShowFollowerDto;
import com.gleb.dto.subscription.ShowFollowingDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Comparator.comparing;

@RestController
@RequestMapping("/users/{username}")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @GetMapping("/followers")
    public Mono<ResponseEntity<List<ShowFollowerDto>>> getFollowers(@PathVariable String username, @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return subscriptionFacade.showFollowersByUsername(username, PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/followings")
    public Mono<ResponseEntity<List<ShowFollowingDto>>> getFollowings(@PathVariable String username, @RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        return subscriptionFacade.showFollowingsByUsername(username, PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

}
