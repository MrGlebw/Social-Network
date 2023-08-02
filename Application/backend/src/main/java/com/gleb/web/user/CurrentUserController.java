package com.gleb.web.user;


import com.gleb.dto.user.UpdateDto;
import com.gleb.dto.user.UserShowDto;
import com.gleb.facade.UserFacade;
import com.gleb.validation.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Slf4j
public class CurrentUserController {

    private final UserFacade userFacade;

    @GetMapping()
    public Mono<UserShowDto> getCurrentUserInfo() {
        return userFacade.getCurrentUserInformation();
    }

    @PatchMapping("/update")
    public Mono<ResponseEntity<String>> updateCurrentUser(@Valid @RequestBody UpdateDto updateDto) {
        UserValidator.ValidationField invalidField = UserValidator.validateUpdatedUser(updateDto);

        if (invalidField != null) {
            String errorMessage = "Invalid " + invalidField.name().toLowerCase() + ": " + updateDto.getFieldValue(invalidField);
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
        } else {
            return userFacade.updateUserByUsername(updateDto)
                    .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("User updated successfully")))
                    .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                    .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @DeleteMapping("/delete")
    public Mono<ResponseEntity<String>> deleteCurrentUser() {
        return userFacade.deleteCurrentUser()
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

}