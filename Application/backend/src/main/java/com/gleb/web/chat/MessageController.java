package com.gleb.web.chat;

import com.gleb.dto.message.MessageShowDto;
import com.gleb.dto.message.MessageSendDto;
import com.gleb.dto.post.CurrentUserPostDto;
import com.gleb.facade.MessageFacade;
import com.gleb.service.MessageService;
import com.gleb.service.user.UserService;
import com.gleb.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static java.util.Comparator.comparing;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class MessageController {


    private final WebSocketSessionManager sessionManager;

    private final MessageFacade messageFacade;

    private final MessageService messageService;

    @PostMapping("/sendMessage")
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

    @GetMapping("/history/{contact}")
    public Mono<ResponseEntity<List<MessageShowDto>>> getChatHistory(@PathVariable String contact) {
        return messageFacade.getChatHistory(contact)
                .sort(comparing(MessageShowDto::getSentAt).reversed())
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}