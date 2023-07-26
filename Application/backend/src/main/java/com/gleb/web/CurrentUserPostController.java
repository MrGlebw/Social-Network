package com.gleb.web;

import com.gleb.data.Post;
import com.gleb.dto.PostForm;
import com.gleb.facade.PostFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @PostMapping("/publish/{id}")
    public Mono<ResponseEntity<String>> publishPost(@PathVariable Integer id) {
        return postFacade.publishPost(id)
                .map(post -> ResponseEntity.status(HttpStatus.OK).body("Post published"));
    }

    @GetMapping("/allPosts")
    public Mono<ResponseEntity<List<Post>>> getAllPosts() {
        return postFacade.getAllPosts()
                .collectList()
                .map(ResponseEntity::ok);
    }
}
