package com.gleb.web.comment;

import com.gleb.data.Comment;
import com.gleb.dto.comment.CommentForm;
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
@RequestMapping("/me")
@RequiredArgsConstructor
@Slf4j
public class CurrentUserCommentController {
    private final CommentFacade commentFacade;

    @PostMapping("/{postId}/commentPost")
    public Mono<ResponseEntity <String>> createComment (@PathVariable Integer postId, @RequestBody CommentForm commentForm) {
        return commentFacade.commentPost(commentForm, postId)
                .map(post -> ResponseEntity.status(HttpStatus.CREATED).body("Comment created"));
    }

    @PatchMapping("/{postId}/updateComment/{commentIdForPost}")
    public Mono<ResponseEntity<String>> updateComment(@PathVariable Integer postId, @PathVariable Integer commentIdForPost,
                                                      @RequestBody CommentForm commentForm) {
        return commentFacade.updateComment(commentForm, postId, commentIdForPost)
                .map(comment -> ResponseEntity.status(HttpStatus.NO_CONTENT).body("Comment updated"))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/myComments")
    public Mono<ResponseEntity<List<CurrentUserCommentDto>>> getMyComments() {
        return commentFacade.getMyComments()
                .collectList()
                .map(comments -> ResponseEntity.status(HttpStatus.OK).body(comments))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{postId}/deleteComment/{commentIdForPost}")
    public Mono<ResponseEntity<String>> deleteComment(@PathVariable Integer postId, @PathVariable Integer commentIdForPost) {
        return commentFacade.deleteComment(postId, commentIdForPost)
                .flatMap(deleted -> {
                    if (deleted) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Comment deleted"));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found"));
                    }
                });
    }


}
