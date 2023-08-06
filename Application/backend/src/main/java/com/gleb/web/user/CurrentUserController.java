package com.gleb.web.user;


import com.gleb.dto.user.FullUpdateDto;
import com.gleb.dto.user.PasswordUpdateDto;
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
    public Mono<ResponseEntity<String>> updateCurrentUser(@Valid @RequestBody FullUpdateDto fullUpdateDto) {
        UserValidator.ValidationField invalidField = UserValidator.validateFullUpdatedUser(fullUpdateDto);

        if (invalidField != null) {
            String errorMessage = "Invalid " + invalidField.name().toLowerCase() + ": " + fullUpdateDto.getFieldValue(invalidField);
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
        } else {
            return userFacade.updateUserByUsername(fullUpdateDto)
                    .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("User updated successfully")))
                    .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                    .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    @PatchMapping("/updatePassword")
    public Mono<ResponseEntity<String>> updateCurrentUserPassword(@Valid @RequestBody PasswordUpdateDto passwordUpdateDto) {
        UserValidator.ValidationField invalidField = UserValidator.validatePasswordUpdate(passwordUpdateDto);

        if (invalidField != null) {
            String errorMessage = "Invalid " + invalidField.name().toLowerCase() + ": " + passwordUpdateDto.getFieldValue(invalidField);
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
        } else {
            return userFacade.updatePassword(passwordUpdateDto)
                    .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Password updated successfully")))
                    .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                    .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }



    @DeleteMapping("/delete")
    public Mono<ResponseEntity<String>> deleteCurrentUser() {
        return userFacade.deleteCurrentUser()
                .flatMap(deleted -> {
                    if (deleted) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Your account deleted successfully"));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
                    }
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error"));
    }

    @PatchMapping("/makePrivate")
    public Mono<ResponseEntity<String>> makePrivate() {
        return userFacade.makePrivate()
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Your account is now private")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PatchMapping("/makePublic")
    public Mono<ResponseEntity<String>> makePublic() {
        return userFacade.makePublic()
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Your account is now public")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

}