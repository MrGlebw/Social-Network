package com.gleb.facade;
import com.gleb.data.User.User;
import com.gleb.dto.user.UserRegisterDTO;
import com.gleb.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private UserRegisterDTO UserToUserRegisterDTO (User user) {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        BeanUtils.copyProperties(user, userRegisterDTO);
        return userRegisterDTO;
    }

    public Mono<ResponseEntity<Object>> getUserByUsername(String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok().body(UserToUserRegisterDTO(user.get())));
    }
    public ResponseEntity<UserRegisterDTO> getMyUser(Authentication authentication) {
        return ResponseEntity.ok(UserToUserRegisterDTO((User) authentication.getPrincipal()));
    }

    public ResponseEntity<UserRegisterDTO> updateUser(UserRegisterDTO userRegisterDTO, Authentication authentication) {
        User user = UserRegisterDTOtoUser(userRegisterDTO);
        return ResponseEntity.ok(UserToUserRegisterDTO(userService.updateUser(user).block()));
    }

    public Mono<Void> deleteUser(String username) {
        return userService.deleteUser(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with username: " + username)));
    }







}