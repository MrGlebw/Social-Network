package com.gleb.service;

import com.gleb.data.TextMessage;
import com.gleb.repo.MessageRepo;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;

    public Mono<TextMessage> sendMessage (TextMessage message) {
        return messageRepo.save(message.toBuilder()
                .sentAt(LocalDateTime.now())
                .build());
    }
}

