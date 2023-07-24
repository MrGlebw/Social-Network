package com.gleb.service;

import com.gleb.data.User;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;



    public Mono<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }



    public Mono<User> registerUser(User user) {

        return userRepo.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .roles(user.getRoles())
                        .enabled(true)
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .build()
        ).doOnSuccess(u -> {
            log.info("IN registerUser - user: {} created", u);
        });
    }

    public Flux<User> findAllByBirthdate (LocalDate birthdate) {
        return userRepo.findAllByBirthdate(birthdate);
    }

    public Mono <User> findByFirstNameAndLastName (String firstName, String lastName) {
        return userRepo.findByFirstNameAndLastName(firstName, lastName);
    }
     public Mono <User> save (User user) {
        return userRepo.save(user);
     }


     public Mono <Void> deleteByUsername (String username) {
        return userRepo.deleteByUsername(username);
     }








}

