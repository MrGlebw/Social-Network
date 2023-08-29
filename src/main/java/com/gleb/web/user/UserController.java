package com.gleb.web.user;


import com.gleb.facade.UserFacade;
import com.gleb.dto.user.UserShowDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

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

    @GetMapping("/id/{id}")
    public Mono<ResponseEntity<UserShowDto>> getUserById(@PathVariable String id) {
        Integer userId = Integer.parseInt(id);
        return userFacade.findById(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/name/{firstName}+{lastName}")
    public Mono<ResponseEntity<List<UserShowDto>>> getUserByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName,
                                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        return userFacade.findByFirstNameAndLastName(firstName, lastName, PageRequest.of(page, size))
                .sort(comparing(UserShowDto::getUsername).reversed())
                .skip((long) page * size).take(size)
                .collect(Collectors.toList())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<List<UserShowDto>>> getAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        return userFacade.findAll(PageRequest.of(page, size))
                .sort(comparing(UserShowDto::getUsername).reversed())
                .skip((long) page * size).take(size)
                .collect(Collectors.toList())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("")
    public Mono<ResponseEntity<List<UserShowDto>>> getAllPublicUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {

        return userFacade.findAllPublicUsers(PageRequest.of(page, size))
                .sort(comparing(UserShowDto::getUsername).reversed())
                .skip((long) page * size).take(size)
                .collect(Collectors.toList())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }






}
