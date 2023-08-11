package com.gleb.websocket;

import com.gleb.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
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

    @Override
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

        return session.send(
                sink.asFlux().map(session::textMessage)
        ).and(session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(message -> sink.tryEmitNext(message))
                .then());
    }



}

