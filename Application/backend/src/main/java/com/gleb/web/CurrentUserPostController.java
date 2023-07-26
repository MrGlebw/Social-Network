package com.gleb.web;

import com.gleb.data.Post;
import com.gleb.dto.PostForm;
import com.gleb.facade.PostFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping(value = "me/posts")
@RequiredArgsConstructor
public class CurrentUserPostController {

    private final PostFacade postFacade;

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createPost(@Validated @RequestBody Mono<PostForm> postForm) {
        return postForm
                .flatMap(postFacade::createPost)
                .map(post -> ResponseEntity.status(HttpStatus.CREATED).body("Post created"));
    }
}
