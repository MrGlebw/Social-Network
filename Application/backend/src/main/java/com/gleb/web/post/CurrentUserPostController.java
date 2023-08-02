package com.gleb.web.post;

import com.gleb.dto.post.CurrentUserPostDto;
import com.gleb.dto.post.PostForm;
import com.gleb.facade.PostFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
                .map(post -> ResponseEntity.status(HttpStatus.CREATED).body("Post created"))
                 .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PatchMapping("/publish/{id}")
    public Mono<ResponseEntity<String>> publishPost(@PathVariable Integer id) {
        return postFacade.publishPost(id)
                .map(post -> ResponseEntity.status(HttpStatus.OK).body("Post published"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    @GetMapping("/allPublishedPosts")
    public Mono<ResponseEntity<List<CurrentUserPostDto>>> getAllPublishedPosts() {
        return postFacade.getAllPublishedPosts()
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/allUnpublishedPosts")
    public Mono<ResponseEntity<List<CurrentUserPostDto>>> getAllUnpublishedPosts() {
        return postFacade.getAllUnpublishedPosts()
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PatchMapping("/update/{id}")
    public Mono<ResponseEntity<String>> updatePost(@PathVariable Integer id, @Validated @RequestBody Mono<PostForm> postForm) {
        return postForm
                .map(post -> postFacade.updatePost(id, post))
                .map(post -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post updated"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> deletePost(@PathVariable Integer id) {
        return postFacade.deleteMyPost(id)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post deleted")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
