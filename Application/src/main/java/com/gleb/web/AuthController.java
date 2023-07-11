package com.gleb.web;

import com.gleb.data.RoleName;
import com.gleb.data.User;
import com.gleb.dto.AuthenticationRequest;
import com.gleb.dto.RegisterRequest;
import com.gleb.repo.UserRepo;
import com.gleb.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/token")
    public Mono<ResponseEntity<Object>> login(@Valid @RequestBody Mono<AuthenticationRequest> authRequest) {
        return authRequest
                .flatMap(login -> authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                        .map(tokenProvider::createToken)
                )
                .map(jwt -> {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                            var tokenBody = Map.of("id_token", jwt);
                            return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                        }
                );
    }


    @PostMapping("/register")
    public Mono<ResponseEntity<Object>> register(@Valid @RequestBody Mono<RegisterRequest> registrationRequest) {
        return registrationRequest.flatMap(request -> {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setBirthdate(request.getBirthdate());
            user.setRoles(Collections.singleton(RoleName.ROLE_USER));

            return userRepo.save(user)
                    .flatMap(savedUser -> {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                savedUser.getUsername(),
                                savedUser.getPassword()
                        );
                        String jwt = tokenProvider.createToken(authentication);

                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                        var tokenBody = Map.of("id_token", jwt);
                        return Mono.just(new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK));
                    });
        });
    }

}