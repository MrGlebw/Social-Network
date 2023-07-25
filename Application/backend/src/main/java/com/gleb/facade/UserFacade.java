package com.gleb.facade;

import com.gleb.data.Roles;
import com.gleb.data.User;
import com.gleb.dto.RegisterRequestDto;
import com.gleb.dto.UpdateDto;
import com.gleb.dto.UserShowDto;
import com.gleb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    public Mono<RegisterRequestDto> registerUser(RegisterRequestDto registerRequestDto, Roles role) {
        User user = registerRequestDtoToUser(registerRequestDto);
        user.setRoles(Collections.singleton(Roles.USER)); // Set the role for the user
        return userService.registerUser(user)
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
                    user.setUsername(username);
                    user.setFirstName(updateDto.getFirstName());
                    user.setLastName(updateDto.getLastName());
                    user.setEmail(updateDto.getEmail());
                    user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
                    user.setUpdated(LocalDateTime.now()); // Set the updated field in the entity
                    return user;
                })
                .flatMap(userService::save);
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


    public Mono<Void> deleteCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.deleteByUsername(username);
                });
    }

    public Mono<UserShowDto> findByUsername(String username) {
        return userService.findUserByUsername(username)
                .map(this::mapToUserShowDto);
    }

    public Mono <UserShowDto> findByFirstNameAndLastName (String firstName, String lastName) {
        return userService.findByFirstNameAndLastName(firstName, lastName)
                .map(this::mapToUserShowDto);
    }

    public Mono <UserShowDto> findById (Integer id) {
        return userService.findById(id)
                .map(this::mapToUserShowDto);
    }





}
