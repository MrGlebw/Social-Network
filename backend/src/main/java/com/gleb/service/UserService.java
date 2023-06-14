package com.gleb.service;

import com.gleb.config.SecurityConfig;
import com.gleb.data.user.RoleName;
import com.gleb.data.user.User;
import com.gleb.exceptions.UserAlreadyExistsException;
import com.gleb.repo.UserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.beans.FeatureDescriptor;
import java.util.*;


@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;

    private final SecurityConfig securityConfig;


    public UserService(UserRepo userRepo, SecurityConfig securityConfig) {
        this.userRepo = userRepo;
        this.securityConfig = securityConfig;
    }

    @Transactional
    public Mono<User> createUser(User user) {
        return userRepo.findByEmail(user.getEmail())
                .flatMap(existingEmail -> {
                    if (existingEmail.isPresent()) {
                        return Mono.error(new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists"));
                    } else {
                        return userRepo.findByUsername(user.getUsername())
                                .flatMap(existingUsername -> {
                                    if (existingUsername.isPresent()) {
                                        return Mono.error(new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists"));
                                    } else {
                                        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
                                        user.setRoles(Collections.singleton(RoleName.ROLE_USER));
                                        return userRepo.save(user);
                                    }
                                });
                    }
                });
    }

    public Mono<Optional<User>> findByUsername(String username) {
        return userRepo.findByUsername(username).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with username: " + username)));
    }

    public Mono<User> updateUser(User user) {
        return userRepo.findByUsername(user.getUsername())
                .flatMap(existingUser -> existingUser.<Mono<? extends User>>map(value -> userRepo.findByEmail(user.getEmail())
                        .flatMap(existingEmail -> {
                            if (existingEmail.isPresent() && !existingEmail.get().getUsername().equals(user.getUsername())) {
                                return Mono.error(new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists"));
                            } else {
                                return userRepo.findByUsername(user.getUsername())
                                        .flatMap(existingUsername -> {
                                            if (existingUsername.isPresent() && !existingUsername.get().getUsername().equals(user.getUsername())) {
                                                return Mono.error(new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists"));
                                            } else {
                                                user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
                                                BeanUtils.copyProperties(user, value, getNullPropertyNames(user));
                                                return userRepo.save(value);
                                            }
                                        });
                            }
                        })).orElseGet(() -> Mono.error(new UsernameNotFoundException("User not found with username: " + user.getUsername()))));

    }
    private String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    @Transactional
    public Mono<Void> deleteUser(String username) {
        return userRepo.findByUsername(username)
                .flatMap(existingUser -> existingUser.map(userRepo::delete).orElseGet(() -> Mono.error(new UsernameNotFoundException("User not found with username: " + username))))
                .then();
    }





}