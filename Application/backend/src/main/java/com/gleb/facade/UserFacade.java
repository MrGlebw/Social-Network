package com.gleb.facade;

import com.gleb.data.Roles;
import com.gleb.data.User;
import com.gleb.dto.RegisterRequestDto;

import com.gleb.dto.UpdateDto;
import com.gleb.dto.UserShowDto;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.UserDetailsServiceImpl;
import com.gleb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;


    public Mono<RegisterRequestDto> registerUser(RegisterRequestDto registerRequestDto, Roles role) {
        User user = registerRequestDtoToUser(registerRequestDto);
        user.setRoles(Collections.singleton(role)); // Set the role for the user
        return userService.save(user)
                .map(this::userToRegisterRequestDto);
    }


    private RegisterRequestDto userToRegisterRequestDto(User user) {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        BeanUtils.copyProperties(user, registerRequestDto);
        return registerRequestDto;
    }

    private User registerRequestDtoToUser(RegisterRequestDto registerRequestDto) {
        User user = new User();
        BeanUtils.copyProperties(registerRequestDto, user);
        return user;
    }



    public Mono<Void> deleteUserByUsername(String username) {
        return userService.deleteByUsername(username);
    }

    public Mono<User> getUserByUsername(String username) {
        return userService.findUserByUsername(username);
    }
    public Mono<UserShowDto> getCurrentUserInformation() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.findUserByUsername(username)
                            .map(this::mapToUserShowDto);
                });
    }

    public Mono<User> updateUserByUsername(String username, UpdateDto updateDto) {
        return userService.findUserByUsername(username)
                .map(user -> {
                    user.setUsername(updateDto.getUsername());
                    user.setFirstName(updateDto.getFirstName());
                    user.setLastName(updateDto.getLastName());
                    user.setEmail(updateDto.getEmail());
                    user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
                    user.setUpdated(updateDto.getUpdated());
                    return user;
                })
                .flatMap(userService::save);
    }

    public UpdateDto userToUpdateDto(User user) {
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName(user.getFirstName());
        updateDto.setLastName(user.getLastName());
        updateDto.setEmail(user.getEmail());
        updateDto.setPassword(user.getPassword());
        updateDto.setUpdated(LocalDateTime.now());
        return updateDto;
    }

    private UserShowDto mapToUserShowDto(User user) {
        UserShowDto userShowDto = new UserShowDto();
        userShowDto.setUsername(user.getUsername());
        userShowDto.setFirstName(user.getFirstName());
        userShowDto.setLastName(user.getLastName());
        userShowDto.setBirthdate(user.getBirthdate());
        userShowDto.setRoles(user.getRoles());
        return userShowDto;
    }





}
