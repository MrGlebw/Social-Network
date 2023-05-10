package com.gleb.facade;

import com.gleb.data.User.User;
import com.gleb.dto.UserRegisterDTO;
import com.gleb.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserFacade {
    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<UserRegisterDTO> createUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        return ResponseEntity.ok().body(userService.createUser(user))
    }
}
