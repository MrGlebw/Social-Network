package com.gleb.web.comment;

import com.gleb.dto.comment.CommentShowDto;
import com.gleb.facade.CommentFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Comparator.comparing;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentFacade commentFacade;

    @GetMapping()
    public Mono<ResponseEntity<List<CommentShowDto>>> getAllComments(@PathVariable Integer postId, @RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        return commentFacade.findAllByPostId(postId, PageRequest.of(page, size))
                .sort(comparing(CommentShowDto::getCreatedDate).reversed())
                .skip((long) page * size).take(size)
                .collectList()
                .map(comments -> ResponseEntity.status(HttpStatus.OK).body(comments))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/author/{authorName}")
    public Mono<ResponseEntity<List<CommentShowDto>>> getAllCommentsOfUser(@PathVariable Integer postId, @PathVariable String authorName, @RequestParam(value = "page", defaultValue = "0") int page,
                                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        return commentFacade.findAllByPostIdAndAuthorName(postId, authorName, PageRequest.of(page, size))
                .sort(comparing(CommentShowDto::getCreatedDate).reversed())
                .skip((long) page * size).take(size)
                .collectList()
                .map(comments -> ResponseEntity.status(HttpStatus.OK).body(comments))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    @DeleteMapping("/deleteComment/{commentIdForPost}")
    public Mono<ResponseEntity<String>> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentIdForPost) {
        return commentFacade.deleteCommentByModerator(postId, commentIdForPost)
                .flatMap(deleted -> {
                    if (deleted) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Comment deleted"));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found"));
                    }
                });
    }
}
