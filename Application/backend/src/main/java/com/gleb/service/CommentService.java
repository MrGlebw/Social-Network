package com.gleb.service;

import com.gleb.data.Comment;
import com.gleb.repo.CommentRepo;
import com.gleb.repo.PostRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepo commentRepo;
    private final PostRepo postRepo;

    @Transactional
    public Mono<Comment> commentPost (Comment comment , Integer postId) {
        return commentRepo.save(
               comment.toBuilder()
                          .postId(postId)
                          .lastModifiedDate(LocalDateTime.now())
                          .createdDate(LocalDateTime.now())
                       .build()
        ).doOnSuccess(p -> {
            log.info("IN commentPost - comment: {} created", p);
        });
    }

    @Transactional
    public Mono<Comment> updateComment(Integer postId, Integer commentIdForPost, String authorName, String newContent) {
        return commentRepo.findByAuthorName(authorName, commentIdForPost, postId)
                .flatMap(comment -> {
                    comment.setLastModifiedDate(LocalDateTime.now());
                    comment.setContent(newContent); // Update the content property of the comment
                    return commentRepo.save(comment);
                });
    }

    public Flux <Comment> findByAuthorName(String authorName) {
        return commentRepo.findByAuthorName(authorName);
    }


    @Transactional
    public Mono<Boolean> deleteComment(Integer postId, Integer commentIdForPost, String authorName) {
        return commentRepo.findByAuthorName(authorName, commentIdForPost, postId)
                .flatMap(comment -> postRepo.findById(postId)
                        .flatMap(post -> {
                            int newCommentsCount = post.getCommentsCount() - 1;
                            if (newCommentsCount >= 0) {
                                post.setCommentsCount(newCommentsCount);
                                return commentRepo.delete(comment)
                                        .then(postRepo.save(post))
                                        .thenReturn(true); // Comment found and deleted successfully
                            } else {
                                return Mono.just(false); // Comment found, but deleting will result in a negative count
                            }
                        }))
                .defaultIfEmpty(false); // Comment not found
    }

    @Transactional
    public Mono<Boolean> deleteCommentByModerator(Integer postId, Integer commentIdForPost) {
        return postRepo.findById(postId)
                        .flatMap(post -> {
                            int newCommentsCount = post.getCommentsCount() - 1;
                            if (newCommentsCount >= 0) {
                                post.setCommentsCount(newCommentsCount);
                                return commentRepo.deleteComment(commentIdForPost, postId)
                                        .then(postRepo.save(post))
                                        .thenReturn(true);
                            } else {
                                return Mono.just(false); // Comment found, but deleting will result in a negative count
                            }
                        })
                        .defaultIfEmpty(false); // Comment not found
    }
   public Flux <Comment> findAllByPostId(Integer postId) {
        return commentRepo.findAllByPostId(postId);
    }

    public Flux <Comment> findAllByPostIdAndAuthorName (Integer postId, String authorName) {
        return commentRepo.findAllByPostIdAndAuthorName(postId, authorName);
    }




}
