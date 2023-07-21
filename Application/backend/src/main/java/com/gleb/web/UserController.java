package com.gleb.web;


import com.gleb.data.User;
import com.gleb.dto.UserShowDto;
import com.gleb.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserFacade userFacade;




    @DeleteMapping("/users/{username}/delete")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable String username) {
        return userFacade.deleteUserByUsername(username)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
