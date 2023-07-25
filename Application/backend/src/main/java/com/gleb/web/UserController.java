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
@RequestMapping("/users")
public class UserController {


    private final UserFacade userFacade;




    @DeleteMapping("/{username}/delete")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable String username) {
        return userFacade.deleteUserByUsername(username)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/username/{username}")
    public Mono<ResponseEntity<UserShowDto>> getUserByUsername(@PathVariable String username) {
        return userFacade.findByUsername(username)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping ("/id/{id}")
    public Mono<ResponseEntity<UserShowDto>> getUserById(@PathVariable String id) {
        Integer userId = Integer.parseInt(id);
        return userFacade.findById(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


}
