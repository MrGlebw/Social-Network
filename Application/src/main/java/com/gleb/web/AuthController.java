package com.gleb.web;



import com.gleb.data.Roles;
import com.gleb.dto.AuthenticationRequestDto;
import com.gleb.dto.RegisterRequestDto;
import com.gleb.facade.UserFacade;
import com.gleb.repo.UserRepo;
import com.gleb.security.JwtProperties;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.UserRefreshTokenService;
import com.gleb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;

    private final UserRefreshTokenService refreshTokenService;

    private final JwtProperties jwtProperties;


    private final UserFacade userFacade;


    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        // Pass the desired role for the user when calling registerUser
        Mono<RegisterRequestDto> registeredUserMono = userFacade.registerUser(registerRequestDto, Roles.USER);

        return registeredUserMono
                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"))
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

                            // Save the refresh token to the database for the authenticated user
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            tokenProvider.saveRefreshTokenToDatabase(refreshToken, userDetails.getUsername());

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
    public Mono<ResponseEntity<Object>> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refresh_token");

        if (tokenProvider.validateRefreshToken(refreshToken)) {
            UserDetails userDetails = tokenProvider.getUserDetailsFromRefreshToken(refreshToken);
            if (userDetails != null) {
                String newAccessToken = tokenProvider.createAccessTokenFromRefreshToken(refreshToken);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
                Map<String, String> response = new HashMap<>();
                response.put("access_token", newAccessToken);
                return Mono.just(ResponseEntity.ok().headers(httpHeaders).body(response));
            }
        }
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}