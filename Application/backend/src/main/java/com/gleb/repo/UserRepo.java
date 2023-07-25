package com.gleb.repo;

import com.gleb.data.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


@Repository
public interface UserRepo extends R2dbcRepository<User, Integer> {
    Mono<User> findByUsername(String username);


    Flux<User> findAllByBirthdate (LocalDate birthdate);

    Flux <User> findByFirstNameAndLastName (String firstName, String lastName);

    Mono <Void> deleteByUsername (String username);



}
