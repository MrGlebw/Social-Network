package com.gleb.web;

import com.gleb.dto.AuthRequestDto;
import com.gleb.dto.AuthResponseDto;
import com.gleb.security.JWTUtil;
import com.gleb.security.PBKDF2Encoder;
import com.gleb.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class AuthREST {


    private JWTUtil jwtUtil;
    private PBKDF2Encoder passwordEncoder;
    private UserService userService;



    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDto>> login(@RequestBody AuthRequestDto ar) {
        return userService.getUserByUsername(ar.getUsername())
                .filter(userDetails -> passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword()))
                .map(userDetails -> ResponseEntity.ok(new AuthResponseDto(jwtUtil.generateToken(userDetails))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

}