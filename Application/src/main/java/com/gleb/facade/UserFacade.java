package com.gleb.facade;

import com.gleb.data.Roles;
import com.gleb.data.User;
import com.gleb.dto.RegisterRequestDto;
import com.gleb.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
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

    public Mono<RegisterRequestDto> registerUser(RegisterRequestDto registerRequestDto, Roles role) {
        User user = registerRequestDtoToUser (registerRequestDto);
        user.setRoles(Collections.singleton(role)); // Set the role for the user
        return userService.registerUser(user)
                .map(this::userToRegisterRequestDto);
    }
}
