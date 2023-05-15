package com.gleb.service;

import com.gleb.config.SecurityConfig;
import com.gleb.data.User.RoleName;
import com.gleb.data.User.User;
import com.gleb.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;

    private final SecurityConfig securityConfig;


    public UserService(UserRepo userRepo, SecurityConfig securityConfig) {
        this.userRepo = userRepo;
        this.securityConfig = securityConfig;
    }

    public Mono<User> createUser(User user){
        return userRepo.findByUsername(user.getUsername())
                .flatMap(existedUsername -> {
                    return Mono.error(new RuntimeException("User with this username already exists"));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    String encodedPassword = securityConfig.passwordEncoder().encode(user.getPassword());
                    user.setPassword(encodedPassword);
                    user.setRoles(Collections.singleton(RoleName.ROLE_USER));
                    return Mono.just(userRepo.save(user));
                }))
                .cast(User.class); // cast the output to Mono<User>
    }
}
