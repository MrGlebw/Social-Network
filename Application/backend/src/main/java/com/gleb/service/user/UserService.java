package com.gleb.service.user;

import com.gleb.data.user.User;
import com.gleb.exceptions.EmailAlreadyTakenException;
import com.gleb.exceptions.UsernameAlreadyTakenException;
import com.gleb.repo.PostRepo;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final PostRepo postRepo;


    public Mono<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }


    public Mono<User> registerUser(User user) {
        String username = user.getUsername();
        String email = user.getEmail();

        return userRepo.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UsernameAlreadyTakenException("Username" + username + " already exists."));
                    } else {
                        return userRepo.existsByEmail(email)
                                .flatMap(existsByEmail -> {
                                    if (existsByEmail) {
                                        return Mono.error(new EmailAlreadyTakenException("Email already exists."));
                                    } else {
                                        return userRepo.save(
                                                user.toBuilder()
                                                        .password(passwordEncoder.encode(user.getPassword()))
                                                        .roles(user.getRoles())
                                                        .enabled(true)
                                                        .created(LocalDateTime.now())
                                                        .updated(LocalDateTime.now())
                                                        .build()
                                        );
                                    }
                                });
                    }
                })
                .doOnSuccess(u -> log.info("IN registerUser - user: {} created", u))
                .onErrorMap(DataIntegrityViolationException.class, ex -> new EmailAlreadyTakenException("Email or username already exist.")); // Optional: Handle DataIntegrityViolationException from userRepo.save() if necessary
    }


    public Flux<User> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        return userRepo.findByFirstNameAndLastName(firstName, lastName);
    }

    @Transactional
    public Mono<User> save(User user) {
        return userRepo.save(user);
    }


    public Mono<Void> deleteByUsername(String username) {
        return userRepo.deleteByUsername(username);
    }


    public Mono<User> findById(Integer id) {
        return userRepo.findById(id);
    }


    public Flux<User> findAll(Pageable pageable) {
        return userRepo.findAll();
    }

    public Flux<User> findAllPublicUsers(Pageable pageable) {
        return userRepo.findAllPublicUsers();
    }

    public Mono<Integer> getPostsCountByAuthor(String authorName) {
        return postRepo.allPostsByAuthorName(authorName)
                .collectList()
                .map(List::size);
    }

    public Mono<Void> updatePostCountForUser(String username) {
        return getPostsCountByAuthor(username)
                .flatMap(postCount -> userRepo.updatePostsCount(username, postCount));
    }

    public Mono<Void> makePrivate(String username) {
        return userRepo.makePrivate(username);
    }

    public Mono<Void> makePublic(String username) {
        return userRepo.makePublic(username);
    }

    public Mono<Void> ban(String username) {
        return userRepo.ban(username);
    }

    public Mono<Void> unban(String username) {
        return userRepo.unban(username);
    }


}

