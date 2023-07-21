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
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;


    public Mono<RegisterRequestDto> registerUser(RegisterRequestDto registerRequestDto, Roles role) {
        User user = registerRequestDtoToUser(registerRequestDto);
        user.setRoles(Collections.singleton(role)); // Set the role for the user
        return userService.registerUser(user)
                .map(this::userToRegisterRequestDto);
    }



    public Mono<String> login(String username, String password) {
        return userDetailsService.findByUsername(username)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .map(UserDetails::getUsername)
                .map(this::generateToken);
    }

    private String generateToken(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        return tokenProvider.createAccessToken(authentication);
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

    private User updateUserDtoToUser(UpdateDto updateDto) {
        User user = new User();
        BeanUtils.copyProperties(updateDto, user);
        return user;

    }

    private UpdateDto userToUpdateDto(User user) {
        UpdateDto updateDto = new UpdateDto();
        BeanUtils.copyProperties(user, updateDto);
        return updateDto;
    }

    public Mono<UpdateDto> updateUserByUsername(String username, UpdateDto userUpdateDto) {
        return userDetailsService.findByUsername(username)
                .flatMap(user -> {
                    updateUserFields((User) user, userUpdateDto);
                    return userService.save((User) user);
                })
                .map(this::userToUpdateDto);
    }

    private void updateUserFields(User user, UpdateDto userUpdateDto) {
        user.setUsername(userUpdateDto.getUsername());
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setBirthdate(LocalDate.parse(userUpdateDto.getBirthdate()));
        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        user.setUpdated(LocalDateTime.now());
        user.setIsPrivate(userUpdateDto.getIsPrivate());

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
