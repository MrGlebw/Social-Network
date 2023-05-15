package com.gleb.facade;
import com.gleb.data.User.User;
import com.gleb.dto.UserRegisterDTO;
import com.gleb.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserFacade {
    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public Mono<ResponseEntity<Object>> createUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        return userService.createUser(user)
                .map(savedUser -> ResponseEntity.ok().body(savedUser.toUserRegisterDTO()));
    }
}
