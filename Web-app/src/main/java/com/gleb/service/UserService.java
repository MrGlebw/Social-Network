package com.gleb.service;

import com.gleb.data.User.User;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> registerUser(User user) {
           return userRepo.findByUsername(user.getUsername())
                    .switchIfEmpty(Mono.defer(() -> {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                        user.setCreatedAt(now());
                        user.setUpdatedAt(now());
                        return userRepo.save(user);
                    }));
        }


    public Mono<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    public Mono<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
