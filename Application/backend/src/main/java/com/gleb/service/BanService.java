package com.gleb.service;

import com.gleb.repo.BanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class BanService {
    private final BanRepo banRepo;

    public Mono<Void> banUser(String toUser) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(fromUser ->
                { if (!fromUser.equals( toUser)){
                   return banRepo.saveByFromUsernameAndToUsername(fromUser, toUser);
                }
                else {
                    return Mono.error(new Exception("You can't ban yourself"));
                }
    }
                )
                .then();
    }

    public Mono<Void> unbanUser(String toUser) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMap(fromUser -> banRepo.deleteByFromUsernameAndToUsername(fromUser, toUser))
                .then();
    }

    public Mono <Boolean> existsByFromUsernameAndToUsername(String fromUser, String toUser) {
        return banRepo.existsByFromUsernameAndToUsername(fromUser, toUser);
    }


    public Flux<String> findBannedBy(Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMapMany(fromUser -> banRepo.findByFromUsername(fromUser, pageable));

    }

    public Flux<String> findWhoBanned(Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getName())
                .flatMapMany(toUser -> banRepo.findByToUsername(toUser, pageable));

    }

}
