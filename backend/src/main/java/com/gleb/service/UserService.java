package com.gleb.service;

import com.gleb.config.SecurityConfig;
import com.gleb.data.User.RoleName;
import com.gleb.data.User.User;
import com.gleb.exceptions.UserAlreadyExistsException;
import com.gleb.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;

    private final SecurityConfig securityConfig;


    public UserService(UserRepo userRepo, SecurityConfig securityConfig) {
        this.userRepo = userRepo;
        this.securityConfig = securityConfig;
    }



    public Mono<User> createUser(User user) {
        return userRepo.findByUsername(user.getUsername())
                .flatMap(user1 -> Mono.<User>error(new UserAlreadyExistsException("User already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
                    user.setRoles(Collections.singleton(RoleName.ROLE_USER));
                    return userRepo.save(user);
                }));
    }
}
