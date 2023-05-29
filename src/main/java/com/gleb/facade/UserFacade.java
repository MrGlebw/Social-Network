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
        User user = UserRegisterDTOtoUser(userRegisterDTO);
        return userService.createUser(user)
                .map(createdUser -> ResponseEntity.ok().body(createdUser));
    }

    private User UserRegisterDTOtoUser (UserRegisterDTO userRegisterDTO) {
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        return user;
    }
}
