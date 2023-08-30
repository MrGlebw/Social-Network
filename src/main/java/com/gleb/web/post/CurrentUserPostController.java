package com.gleb.web.post;

import com.gleb.facade.PostFacade;
import com.gleb.dto.post.CurrentUserPostDto;
import com.gleb.dto.post.PostForm;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Comparator.comparing;

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
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No post with id " + id));
    }


    @GetMapping("/allPublishedPosts")
    public Mono<ResponseEntity<List<CurrentUserPostDto>>> getAllPublishedPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                               @RequestParam(value = "size", defaultValue = "10") int size) {
        return postFacade.getAllPublishedPosts(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/allUnpublishedPosts")
    public Mono<ResponseEntity<List<CurrentUserPostDto>>> getAllUnpublishedPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        return postFacade.getAllUnpublishedPosts(PageRequest.of(page, size))
                .skip((long) page * size).take(size)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/update/{id}")
    public Mono<ResponseEntity<String>> updatePost(@PathVariable Integer id, @RequestBody Mono<PostForm> postForm) {
        return postForm
                .flatMap(post -> postFacade.updatePost(id, post))
                .map(post -> ResponseEntity.status(HttpStatus.OK).body("Post updated"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> deletePost(@PathVariable Integer id) {
        return postFacade.deleteMyPost(id)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post deleted")))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
