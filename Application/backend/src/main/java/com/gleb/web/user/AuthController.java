package com.gleb.web.user;



import com.gleb.dto.user.AuthenticationRequestDto;
import com.gleb.dto.user.RegisterRequestDto;
import com.gleb.facade.UserFacade;
import com.gleb.security.JwtProperties;
import com.gleb.security.JwtTokenProvider;
import com.gleb.validation.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    public Mono<ResponseEntity<String>> registerUser(@Valid @RequestBody Mono<RegisterRequestDto> registerRequestDtoMono) {
        return registerRequestDtoMono
                .flatMap(registerRequestDto -> {
                    // Perform validation on the RegisterRequestDto using UserValidator
                    UserValidator.ValidationField invalidField = UserValidator.validateRegisteredUser(registerRequestDto);

                    if (invalidField != null) {
                        String errorMessage = "Invalid " + invalidField.name().toLowerCase() + ": " + registerRequestDto.getFieldValue(invalidField);
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
                    } else {
                        // If there are no validation errors, proceed with user registration
                        return userFacade.registerUser(registerRequestDto)
                                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"))
                                .onErrorResume(DataIntegrityViolationException.class, ex -> {
                                    String usernameErrorMessage = "Username or email already exists.";
                                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(usernameErrorMessage));
                                })
                                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

    @PostMapping("/registerAdmin")
    public Mono<ResponseEntity<String>> registerAdmin(@Valid @RequestBody Mono<RegisterRequestDto> registerRequestDtoMono) {
        return registerRequestDtoMono
                .flatMap(registerRequestDto -> {
                    // Perform validation on the RegisterRequestDto using UserValidator
                    UserValidator.ValidationField invalidField = UserValidator.validateRegisteredUser(registerRequestDto);

                    if (invalidField != null) {
                        String errorMessage = "Invalid " + invalidField.name().toLowerCase() + ": " + registerRequestDto.getFieldValue(invalidField);
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
                    } else {
                        // If there are no validation errors, proceed with user registration
                        return userFacade.registerAdmin(registerRequestDto)
                                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"))
                                .onErrorResume(DataIntegrityViolationException.class, ex -> {
                                    String usernameErrorMessage = "Username or email already exists.";
                                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(usernameErrorMessage));
                                })
                                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

    @PostMapping("/registerModerator")
    public Mono<ResponseEntity<String>> registerModerator(@Valid @RequestBody Mono<RegisterRequestDto> registerRequestDtoMono) {
        return registerRequestDtoMono
                .flatMap(registerRequestDto -> {
                    // Perform validation on the RegisterRequestDto using UserValidator
                    UserValidator.ValidationField invalidField = UserValidator.validateRegisteredUser(registerRequestDto);

                    if (invalidField != null) {
                        String errorMessage = "Invalid " + invalidField.name().toLowerCase() + ": " + registerRequestDto.getFieldValue(invalidField);
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
                    } else {
                        // If there are no validation errors, proceed with user registration
                        return userFacade.registerModerator(registerRequestDto)
                                .map(registeredUser -> ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"))
                                .onErrorResume(DataIntegrityViolationException.class, ex -> {
                                    String usernameErrorMessage = "Username or email already exists.";
                                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(usernameErrorMessage));
                                })
                                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@Valid @RequestBody Mono<AuthenticationRequestDto> authRequest) {
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
                })
                .onErrorResume(BadCredentialsException.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Invalid username or password");

                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
                })
                .onErrorResume(AuthenticationException.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Authentication failed");

                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
                })
                .onErrorResume(Throwable.class, ex -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Internal Server Error");

                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
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