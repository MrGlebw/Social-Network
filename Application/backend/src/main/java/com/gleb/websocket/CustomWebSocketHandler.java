package com.gleb.websocket;

import com.gleb.data.TextMessage;
import com.gleb.facade.MessageFacade;
import com.gleb.security.JwtTokenProvider;
import com.gleb.service.BanService;
import com.gleb.service.MessageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<String> sink;

    private final WebSocketSessionManager sessionManager;

    private final JwtTokenProvider tokenProvider;

    private final MessageService messageService;

    private final BanService banService;

    private final MessageFacade messageFacade;


    @Override
    @NonNull
    public Mono<Void> handle(WebSocketSession session) {
        String jwtToken = session.getHandshakeInfo().getHeaders().getFirst("Authorization");

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7); // Remove "Bearer " prefix
            if (tokenProvider.validateToken(jwtToken)) {
                Authentication authentication = tokenProvider.getAuthentication(jwtToken);
                String username = authentication.getName();
                session.getAttributes().put("username", username);
                sessionManager.addUserSession(username, session);
            }
        }

        String currentUser = (String) session.getAttributes().get("username");
        String recipient = (String) session.getAttributes().get("recipient");

        // Handle sending messages based on ban checks
        Mono<Void> sendMessageMono = session.receive()
                .flatMap(message -> {
                    return banService.existsByFromUsernameAndToUsername(currentUser, recipient)
                            .flatMap(bannedBySender -> {
                                if (bannedBySender) {
                                    // The sender banned the recipient, so the recipient should receive nothing
                                    return Mono.error(new RuntimeException("You have banned the recipient"));
                                } else {
                                    return banService.existsByFromUsernameAndToUsername(recipient, currentUser)
                                            .flatMap(bannedByRecipient -> {
                                                if (bannedByRecipient) {
                                                    // The recipient banned the sender, so the sender should receive an exception
                                                    return Mono.error(new RuntimeException("Recipient has banned you"));
                                                } else {
                                                    // Neither banned, so proceed to emit the message
                                                    sink.tryEmitNext(message.getPayloadAsText());
                                                    return Mono.empty();
                                                }
                                            });
                                }
                            });
                }).then();

        return sendMessageMono.then();
    }
}
