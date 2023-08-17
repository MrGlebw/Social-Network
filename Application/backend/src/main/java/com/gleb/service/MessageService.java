package com.gleb.service;

import com.gleb.data.TextMessage;
import com.gleb.repo.BanRepo;
import com.gleb.repo.MessageRepo;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;
    private final BanService banService;

    public Mono<TextMessage> sendMessage(TextMessage message) {
        return banService.existsByFromUsernameAndToUsername(message.getRecipient(), message.getSender())
                .flatMap(banned -> {
                    if (banned) {
                        return Mono.error(new RuntimeException("You are banned"));
                    } else {
                        return messageRepo.save(message.toBuilder()
                                .sentAt(LocalDateTime.now())
                                .build());
                    }
                });
    }

    public Flux<TextMessage> getChatHistory(String username1 , String username2) {
        return messageRepo.findAllByTwoUsernames(username1, username2);
    }












}

