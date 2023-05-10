package com.gleb.service;

import com.gleb.data.User.RoleName;
import com.gleb.data.User.User;
import com.gleb.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User createUser(User user){
        Optional<User> existedUsername = userRepo.findByUsername(user.getUsername());
        if(existedUsername.isPresent()){
           throw new RuntimeException("");
        }
        user.setRoles(Collections.singleton(RoleName.ROLE_USER));
        return userRepo.save(user);
    }
}
