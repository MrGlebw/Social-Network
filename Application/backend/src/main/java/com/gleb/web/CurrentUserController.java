package com.gleb.web;


import com.gleb.data.User;
import com.gleb.dto.UpdateDto;
import com.gleb.dto.UserShowDto;
import com.gleb.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class CurrentUserController {

    private final UserFacade userFacade;

    @GetMapping()
    public Mono<UserShowDto> getCurrentUserInfo() {
        return userFacade.getCurrentUserInformation();
    }

    @PatchMapping("/update")
    public Mono<ResponseEntity<Void>> updateCurrentUser(@AuthenticationPrincipal Mono<User> currentUserMono,
                                                        @RequestBody UpdateDto userUpdateDto) {
        return currentUserMono
                .flatMap(currentUser -> userFacade.updateUserByUsername(currentUser.getUsername(), userUpdateDto))
                .flatMap(updatedUser -> Mono.just(ResponseEntity.noContent().<Void>build()))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().<Void>build()))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build());
    }
}

