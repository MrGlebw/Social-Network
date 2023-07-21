package com.gleb.web;


import com.gleb.data.Roles;
import com.gleb.data.User;
import com.gleb.dto.RegisterRequestDto;
import com.gleb.dto.UpdateDto;
import com.gleb.dto.UserShowDto;
import com.gleb.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Slf4j
public class CurrentUserController {

    private final UserFacade userFacade;

    @GetMapping()
    public Mono<UserShowDto> getCurrentUserInfo() {
        return userFacade.getCurrentUserInformation();
    }

    @PatchMapping("/update")
    public Mono<ResponseEntity<String>> updateCurrentUser(@RequestBody UpdateDto updateDto) {
        return userFacade.updateUserByUsername( updateDto.getUsername(), updateDto)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("User updated successfully")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }
}
