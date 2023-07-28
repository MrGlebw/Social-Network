package com.gleb.web.post;

import com.gleb.data.Post;
import com.gleb.dto.post.PostShowDto;
import com.gleb.facade.PostFacade;
import com.gleb.repo.CommentRepo;
import com.gleb.repo.PostRepo;
import com.gleb.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController()
@RequestMapping(value = "/feed")
@RequiredArgsConstructor
@Validated
public class PostController {


    private final CommentRepo commentRepo;

    private final PostFacade postFacade;

    private final PostService postService;

    @GetMapping("")
    public Mono<ResponseEntity<List<PostShowDto>>> all(@RequestParam(value = "q", required = false) String q,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

        if (StringUtils.hasText(q)) {
            return postFacade.findByTitleContains(q, PageRequest.of(page, size, sort))
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        } else {
            // Use the getFeed method directly to get published posts
            return postFacade.getFeed(PageRequest.of(page, size, sort))
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/moderatorFeed")
    public Mono<ResponseEntity<List<Post>>> moderatorFeed(@RequestParam(value = "q", required = false) String q,
                                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        if (StringUtils.hasText(q)) {
            return postService.findByTitleContains(q, PageRequest.of(page, size, sort))
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        } else {
            // Use the getPublishedPosts method directly to get all published posts
            return postService.getFeed(PageRequest.of(page, size, sort))
                    .collectList()
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build());
        }
    }



    @DeleteMapping("/moderatorFeed/delete/{id}")
    public Mono<ResponseEntity<String>> deletePost(@PathVariable Integer id) {
        return postFacade.deletePost(id)
                .flatMap(deleted -> {
                    if (!deleted) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post deleted"));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found"));
                    }
                });
    }

    @PatchMapping("/moderatorFeed/disapprove/{id}")
    public Mono<ResponseEntity<String>> disapprovePost(@PathVariable Integer id) {
        return postFacade.disapprovePost(id)
                .flatMap(disapproved -> {
                    if (disapproved) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post disapproved"));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found or disapproved"));
                    }
                });
    }



}

