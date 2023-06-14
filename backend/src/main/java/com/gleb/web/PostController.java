package com.gleb.web;

import com.gleb.data.Post;
import com.gleb.facade.PostFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users/{userId}/posts")
public class PostController {
 private final PostFacade postFacade;


    public PostController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Post>>createPost(@RequestBody Post post) {
        return postFacade.createPost(post);
    }

    @GetMapping("/{id}/")
    public Flux<ResponseEntity<Post>> getPost(@PathVariable Long id, @PathVariable String userId) {
        return postFacade.getPost(id);
    }

    @PutMapping("/{id}/")
    public Mono<ResponseEntity<Post>> updatePost(@RequestBody Post post, @PathVariable Long id, @PathVariable String userId) {
        return postFacade.updatePost(post);
    }

    @DeleteMapping("/{id}/")
    public Mono<Void> deletePost(@PathVariable Long id, @PathVariable String userId) {
        return postFacade.deletePost(id);
    }
}
