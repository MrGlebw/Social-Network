package com.gleb.web;


import com.gleb.dto.user.UserRegisterDTO;
import com.gleb.facade.UserFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/api/v1/users")
public class UserController {


    private final UserFacade userFacade;


    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Object>> createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userFacade.createUser(userRegisterDTO);
    }

    @GetMapping
    public ResponseEntity<UserRegisterDTO> getMyUser(Authentication authentication) {
        return userFacade.getMyUser(authentication);
    }

    @PutMapping
    public ResponseEntity<UserRegisterDTO> updateUser(@RequestBody UserRegisterDTO userRegisterDTO, Authentication authentication) {
        return userFacade.updateUser(userRegisterDTO, authentication);
    }

    @DeleteMapping
    public Mono<Void> deleteUser(Authentication authentication) {
        return userFacade.deleteUser(authentication.getName());
    }

}

