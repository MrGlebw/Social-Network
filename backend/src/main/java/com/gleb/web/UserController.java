package com.gleb.web;

import com.gleb.dto.UserRegisterDTO;
import com.gleb.facade.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> createUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userFacade.createUser(userRegisterDTO);
    }
}


