package com.gleb.web;

import com.gleb.data.User;
import com.gleb.dto.AuthenticationRequestDto;
import com.gleb.dto.RegisterRequestDto;
import com.gleb.facade.UserFacade;
import com.gleb.repo.UserRepo;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;


    private final UserFacade userFacade;


    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@Valid @RequestBody Mono<AuthenticationRequestDto> authRequest) {

        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.getUsername(), login.getPassword()))
                        .map(this.tokenProvider::createToken))
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("access_token", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                });

    }


    @PostMapping("/register")
    public Mono<ResponseEntity> registerUser (@RequestBody RegisterRequestDto registerRequestDto) {
        return userFacade.registerUser(registerRequestDto)
                .thenReturn(ResponseEntity.ok("User registered successfully"));


    }
}