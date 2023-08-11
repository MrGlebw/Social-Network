package com.gleb.web.chat;

import com.gleb.data.TextMessage;
import com.gleb.dto.message.MessageSendDto;
import com.gleb.facade.MessageFacade;
import com.gleb.service.user.UserService;
import com.gleb.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class MessageController {


    private final WebSocketSessionManager sessionManager;

    private final UserService userService;

    private final MessageFacade messageFacade;

    @PostMapping("/sendMessage/")
    public Mono<ResponseEntity<String>> sendMessage(@RequestBody Mono<MessageSendDto> message) {
        return message.flatMap(messageSendDto -> {
            WebSocketSession recipientSession = sessionManager.getUserSession(messageSendDto.getRecipient());

            if (recipientSession != null && recipientSession.isOpen()) {
                String content = messageSendDto.getContent();
                return recipientSession.send(Mono.just(recipientSession.textMessage(content)))
                        .then(messageFacade.sendMessage(messageSendDto))
                        .map(textMessage -> ResponseEntity.ok("Message sent"))
                        .defaultIfEmpty(ResponseEntity.badRequest().body("Message not sent"));
            } else {
                return Mono.just(ResponseEntity.badRequest().body("Recipient session not available"));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}