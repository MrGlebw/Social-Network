package com.gleb.wrapper;

import com.gleb.repo.UserRepo;
import com.gleb.data.user.Roles;
import com.gleb.data.user.User;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class UserWrapperService {

    private final UserRepo userRepo;

    public UserWrapperService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    public Mono<UserWrapper> findUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .map(this::wrapUser);
    }

    private UserWrapper wrapUser(User user) {
        String rolesAsString = user.getRoles().stream()
                .map(Roles::name)
                .collect(Collectors.joining(","));

        return new UserWrapper(user.getUsername(), rolesAsString);
    }
}
