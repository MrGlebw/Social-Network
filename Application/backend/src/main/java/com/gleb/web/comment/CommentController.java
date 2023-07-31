package com.gleb.web.comment;

import com.gleb.dto.comment.CommentForm;
import com.gleb.dto.comment.CommentShowDto;
import com.gleb.dto.comment.CurrentUserCommentDto;
import com.gleb.facade.CommentFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentFacade commentFacade;

    @GetMapping()
    public Mono<ResponseEntity<List<CommentShowDto>>> getAllComments(@PathVariable Integer postId) {
        return commentFacade.findAllByPostId(postId)
                .collectList()
                .map(comments -> ResponseEntity.status(HttpStatus.OK).body(comments))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/{authorName}")
    public Mono<ResponseEntity<List<CommentShowDto>>> getAllCommentsOfUser(@PathVariable Integer postId, @PathVariable String authorName) {
        return commentFacade.findAllByPostIdAndAuthorName(postId, authorName)
                .collectList()
                .map(comments -> ResponseEntity.status(HttpStatus.OK).body(comments))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    @DeleteMapping("deleteComment/{commentIdForPost}")
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
