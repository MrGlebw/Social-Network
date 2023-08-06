package com.gleb.facade;

import com.gleb.data.user.Roles;
import com.gleb.data.user.User;
import com.gleb.dto.user.PasswordUpdateDto;
import com.gleb.dto.user.RegisterRequestDto;
import com.gleb.dto.user.FullUpdateDto;
import com.gleb.dto.user.UserShowDto;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;


    public Mono<RegisterRequestDto> registerUser(RegisterRequestDto registerRequestDto) {
        User user = registerRequestDtoToUser(registerRequestDto);
        user.setRoles(Collections.singleton(Roles.USER)); // Set the role for the user
        return userService.registerUser(user)
                .map(this::userToRegisterRequestDto);
    }

    public Mono<RegisterRequestDto> registerAdmin (RegisterRequestDto registerRequestDto) {
        User user = registerRequestDtoToUser(registerRequestDto);
        user.setRoles(Collections.singleton(Roles.ADMIN));
        return userService.registerUser(user)
                .map(this::userToRegisterRequestDto);
    }

    public Mono<RegisterRequestDto> registerModerator (RegisterRequestDto registerRequestDto) {
        User user = registerRequestDtoToUser(registerRequestDto);
        user.setRoles(Collections.singleton(Roles.MODERATOR));
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

    public Mono<User> updateUserByUsername(FullUpdateDto fullUpdateDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.findUserByUsername(username)
                            .map(user -> {
                                user.setFirstName(fullUpdateDto.getFirstName());
                                user.setLastName(fullUpdateDto.getLastName());
                                user.setEmail(fullUpdateDto.getEmail());
                                user.setUpdated(LocalDateTime.now()); // Set the updated field in the entity
                                return user;
                            })
                .flatMap(userService::save);
    });
    }


    public Mono<User> updatePassword(PasswordUpdateDto passwordUpdateDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.findUserByUsername(username)
                            .flatMap(user -> {
                                String newPasswordHash = passwordEncoder.encode(passwordUpdateDto.getNewPassword());

                                // Check if the new password is the same as the old one
                                if (user.getPassword().equals(newPasswordHash)) {
                                    return Mono.error(new IllegalArgumentException("New password cannot be the same as the old one."));
                                }

                                // Check if the old password is correct
                                if (!passwordEncoder.matches(passwordUpdateDto.getPassword(), user.getPassword())) {
                                    return Mono.error(new IllegalArgumentException("Old password is incorrect."));
                                }

                                // Check if the new password and new password confirmation match
                                if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getNewPasswordConfirm())) {
                                    return Mono.error(new IllegalArgumentException("New password and new password confirmation do not match."));
                                }

                                // Update the user's password and set the updated field
                                user.setPassword(newPasswordHash);
                                user.setUpdated(LocalDateTime.now());
                                return userService.save(user);
                            });
                });
    }


    public Mono <Void> makePrivate () {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.makePrivate(username);
                });
    }

    public Mono <Void> makePublic () {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.makePublic(username);
                });
    }





    public Mono<Boolean> deleteCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    String username = authentication.getName();
                    return userService.deleteByUsername(username)
                            .thenReturn(true) // Return true if deletion was successful
                            .onErrorResume(UsernameNotFoundException.class, ex -> Mono.just(false));
                });

    }

    public Mono <Void> ban (String username) {
        return userService.ban(username);
    }

    public Mono <Void> unban (String username) {
        return userService.unban(username);
    }





    public Mono<UserShowDto> findByUsername(String username) {
        return userService.findUserByUsername(username)
                .map(this::mapToUserShowDto);
    }

    public Flux<UserShowDto> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        return userService.findByFirstNameAndLastName(firstName, lastName , pageable)
                .map(this::mapToUserShowDto);
    }

    public Mono <UserShowDto> findById (Integer id) {
        return userService.findById(id)
                .map(this::mapToUserShowDto);
    }

    public Flux <UserShowDto> findAll (Pageable pageable) {
        return userService.findAllPublicUsers(pageable)
                .map(this::mapToUserShowDto);
    }

    public Flux <UserShowDto> findAllPublicUsers (Pageable pageable) {
        return userService.findAllPublicUsers(pageable)
                .map(this::mapToUserShowDto);
    }


    private UserShowDto mapToUserShowDto(User user) {
        UserShowDto userShowDto = new UserShowDto();
        BeanUtils.copyProperties(user, userShowDto);
        return userShowDto;
    }





}
