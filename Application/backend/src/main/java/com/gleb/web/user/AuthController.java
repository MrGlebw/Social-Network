package com.gleb.web.user;



import com.gleb.dto.user.AuthenticationRequestDto;
import com.gleb.dto.user.RegisterRequestDto;
import com.gleb.facade.UserFacade;
import com.gleb.security.JwtProperties;
import com.gleb.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;


    private final JwtProperties jwtProperties;


    private final UserFacade userFacade;


    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        Mono<RegisterRequestDto> registeredUserMono = userFacade.registerUser(registerRequestDto);

        return registeredUserMono
                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/registerAdmin")
    public Mono<ResponseEntity<String>> registerAdmin(@RequestBody RegisterRequestDto registerRequestDto) {
        Mono<RegisterRequestDto> registeredUserMono = userFacade.registerAdmin(registerRequestDto);

        return registeredUserMono
                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("Admin registered successfully"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/registerModerator")
    public Mono<ResponseEntity<String>> registerModerator(@RequestBody RegisterRequestDto registerRequestDto) {
        Mono<RegisterRequestDto> registeredUserMono = userFacade.registerModerator(registerRequestDto);

        return registeredUserMono
                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("Moderator registered successfully"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@Valid @RequestBody Mono<AuthenticationRequestDto> authRequest) {
        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.getUsername(), login.getPassword()))
                        .map(authentication -> {
                            String accessToken = tokenProvider.createAccessToken(authentication);
                            String refreshToken = tokenProvider.createRefreshToken();

                            Map<String, String> tokens = new HashMap<>();
                            tokens.put("access_token", accessToken);
                            tokens.put("refresh_token", refreshToken);

                            return tokens;
                        }))
                .map(tokens -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.get("access_token"));
                    return new ResponseEntity<>(tokens, httpHeaders, HttpStatus.OK);
                });
    }


        @PostMapping("/refresh")
        public Mono<ResponseEntity<Object>> refreshToken(@RequestBody Mono<Map<String, String>> refreshTokenMapMono) {
            return refreshTokenMapMono.flatMap(refreshTokenMap -> {
                String refreshToken = refreshTokenMap.get("refresh_token");
                if (StringUtils.hasText(refreshToken) && tokenProvider.validateRefreshToken(refreshToken)) {
                    Authentication authentication = tokenProvider.getAuthentication(refreshToken);
                    String newAccessToken = tokenProvider.createAccessToken(authentication);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", newAccessToken);

                    return Mono.just(new ResponseEntity<>(tokens, HttpStatus.OK));
                } else {
                    return Mono.just(new ResponseEntity<>("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED));
                }
            });
        }



    }