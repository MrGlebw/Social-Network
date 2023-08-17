package com.gleb.web.user;

import com.gleb.service.BanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("me/")
@RequiredArgsConstructor
public class BanController {

    private final BanService banService;

    @PostMapping("ban/{username}")
    public Mono<ResponseEntity<String>> banUser(@PathVariable String username) {
        return banService.banUser(username)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.OK).body("User banned successfully")))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("unban/{username}")
    public Mono<ResponseEntity<String>> unbanUser(@PathVariable String username) {
        return banService.unbanUser(username)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.OK).body("User unbanned successfully")))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("bannedByMe")
    public Mono<ResponseEntity<List<String>>> findBannedByMe(@RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return banService.findBannedBy(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("bannedBy")
    public Mono<ResponseEntity<List<String>>> findWhoBannedMe(@RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        return banService.findWhoBanned(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
