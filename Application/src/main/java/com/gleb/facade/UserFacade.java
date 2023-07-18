package com.gleb.facade;

import com.gleb.data.Roles;
import com.gleb.data.User;
import com.gleb.dto.RegisterRequestDto;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Mono<RegisterRequestDto> registerUser(RegisterRequestDto registerRequestDto, Roles role) {
        User user = registerRequestDtoToUser (registerRequestDto);
        user.setRoles(Collections.singleton(role)); // Set the role for the user
        return userService.registerUser(user)
                .map(this::userToRegisterRequestDto);
    }

    public Mono<String> login(String username, String password) {
        return userService.findByUsername(username)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .map(UserDetails::getUsername)
                .map(this::generateToken);
    }

    private String generateToken(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        return tokenProvider.createAccessToken(authentication);
    }

    public Mono<String> refresh(String refreshToken) {
        return userService.findByRefreshToken(refreshToken)
                .map(User::getUsername)
                .map(this::generateToken);
    }

    public Mono<Void> logout() {
        return (Mono<Void>) ReactiveSecurityContextHolder.clearContext();
    }

    private RegisterRequestDto userToRegisterRequestDto(User user) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        BeanUtils.copyProperties(user, registerRequestDto);
        return registerRequestDto;
    }

    private User registerRequestDtoToUser(RegisterRequestDto registerRequestDto) {
        User user = new User();
        BeanUtils.copyProperties( registerRequestDto , user);
        return user;
    }
}
