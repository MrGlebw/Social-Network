package com.gleb.facade;

import com.gleb.data.TextMessage;
import com.gleb.dto.message.MessageSendDto;
import com.gleb.dto.message.MessageShowDto;
import com.gleb.service.MessageService;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MessageFacade {

private final UserService userService;
private final MessageService messageService;
    public Mono<TextMessage> sendMessage(MessageSendDto messageSendDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> {
                    TextMessage textMessage = messageSendDtoToMessage(messageSendDto);;
                    textMessage.setSender(user.getUsername());
                    return messageService.sendMessage(textMessage);
                });
    }

    public Flux<MessageShowDto> getChatHistory(String contact) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMapMany(currentUser -> messageService.getChatHistory(currentUser, contact))
                .map(this::messageToMessageShowDto);
    }





    private TextMessage messageSendDtoToMessage(MessageSendDto messageSendDto) {
        TextMessage message = new TextMessage();
        BeanUtils.copyProperties(messageSendDto, message);
        return message;
    }

    private MessageShowDto messageToMessageShowDto(TextMessage message) {
        MessageShowDto messageShowDto = new MessageShowDto();
        BeanUtils.copyProperties(message, messageShowDto);
        return messageShowDto;
    }
}
