package com.gleb.web;

import com.gleb.data.Message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

public class ResourceREST {
    @GetMapping("/resource/user")
    @PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<Message>> user() {
        return Mono.just(ResponseEntity.ok(new Message("Content for user")));
    }

    @GetMapping("/resource/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Message>> admin() {
        return Mono.just(ResponseEntity.ok(new Message("Content for admin")));
    }

    @GetMapping("/resource/user-or-admin")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Mono<ResponseEntity<Message>> userOrAdmin() {
        return Mono.just(ResponseEntity.ok(new Message("Content for user or admin")));
    }
}
