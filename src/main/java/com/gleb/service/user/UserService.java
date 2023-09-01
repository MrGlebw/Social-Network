package com.gleb.service.user;

import com.gleb.exceptions.EmailAlreadyTakenException;
import com.gleb.exceptions.UserNotFoundException;
import com.gleb.exceptions.UsernameAlreadyTakenException;
import com.gleb.data.user.User;
import com.gleb.repo.PostRepo;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
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
@CacheConfig(cacheNames = "userCache")
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final PostRepo postRepo;



    @Cacheable(cacheNames = "user", key = "#username", unless = "#result == null")
    public Mono<User> findUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .doOnSuccess(user -> log.info("User found by username: {}", user))
                .switchIfEmpty(Mono.error(new UserNotFoundException(username)));
    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public Mono<User> registerUser(User user) {
        String username = user.getUsername();
        String email = user.getEmail();

        return userRepo.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UsernameAlreadyTakenException(username));
                    } else {
                        return userRepo.existsByEmail(email)
                                .flatMap(existsByEmail -> {
                                    if (existsByEmail) {
                                        return Mono.error(new EmailAlreadyTakenException(email));
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

    @Cacheable(cacheNames = "users")
    public Flux<User> findByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        return userRepo.findByFirstNameAndLastName(firstName, lastName);
    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    @Transactional
    public Mono<User> save(User user) {
        return userRepo.save(user);
    }


    @Caching(evict = { @CacheEvict(cacheNames = "user", key = "#username"),
            @CacheEvict(cacheNames = "users", allEntries = true) })
    public Mono<Void> deleteByUsername(String username) {
        return userRepo.deleteByUsername(username);
    }



    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    public Mono<User> findById(Integer id) {
        return userRepo.findById(id);
    }



    @Cacheable(cacheNames = "users")
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



    public Mono<Boolean> existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }
}

