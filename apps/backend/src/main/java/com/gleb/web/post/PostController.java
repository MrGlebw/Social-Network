package com.gleb.web.post;

import com.gleb.data.post.Post;
import com.gleb.facade.PostFacade;
import com.gleb.service.PostService;
import com.gleb.dto.post.PostShowDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Comparator.comparing;

@RestController()
@RequestMapping(value = "/feed")
@RequiredArgsConstructor
@Validated
public class PostController {



    private final PostFacade postFacade;

    private final PostService postService;

    @GetMapping("")
    public Mono<ResponseEntity<List<PostShowDto>>> all(@RequestParam(value = "q", required = false) String q,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {

        if (StringUtils.hasText(q)) {
            return postFacade.findByTitleContains(q, PageRequest.of(page, size))
                    .sort(comparing(PostShowDto::getPublishedDate).reversed())
                    .skip((long) page * size).take(size)
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        } else {
            return postFacade.getFeed(PageRequest.of(page, size))
                    .sort(comparing(PostShowDto::getPublishedDate).reversed())
                    .skip((long) page * size).take(size)
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/moderatorFeed")
    public Mono<ResponseEntity<List<Post>>> moderatorFeed(@RequestParam(value = "q", required = false) String q,
                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "size", defaultValue = "10") int size) {

        if (StringUtils.hasText(q)) {
            return postService.findByTitleContains(q, PageRequest.of(page, size))
                    .sort(comparing(Post::getPublishedDate).reversed())
                    .skip((long) page * size).take(size)
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        } else {
            return postService.getFeed(PageRequest.of(page, size))
                    .sort(comparing(Post::getPublishedDate).reversed())
                    .skip((long) page * size).take(size)
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                    .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }


    @DeleteMapping("/moderatorFeed/delete/{id}")
    public Mono<ResponseEntity<String>> deletePost(@PathVariable Integer id) {
        return postFacade.deletePost(id)
                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.OK).body("Post deleted")))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());


    }

    @PatchMapping("/moderatorFeed/disapprove/{id}")
    public Mono<ResponseEntity<String>> disapprovePost(@PathVariable Integer id) {
        return postFacade.disapprovePost(id)
                .then(Mono.defer(() -> Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.OK).body("Post disapproved"))))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


}

