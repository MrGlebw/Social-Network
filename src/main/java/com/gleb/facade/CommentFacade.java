package com.gleb.facade;

import com.gleb.repo.PostRepo;
import com.gleb.service.CommentService;
import com.gleb.service.PostService;
import com.gleb.service.user.UserService;
import com.gleb.data.Comment;
import com.gleb.dto.comment.CommentForm;
import com.gleb.dto.comment.CommentShowDto;
import com.gleb.dto.comment.CurrentUserCommentDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentFacade {

    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    private final PostRepo postRepo;

    @Transactional
    public Mono<Object> commentPost(CommentForm commentForm, Integer postId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> {
                    Comment comment = commentFormToComment(commentForm);
                    comment.setAuthorName(user.getUsername());
                    comment.setLastModifiedBy(user.getUsername());

                    // Get the current comments count for the post
                    return postService.findById(postId)
                            .flatMap(post -> {
                                int newCommentsCount = post.getCommentsCount() + 1;
                                post.setCommentsCount(newCommentsCount);
                                comment.setCommentIdForPost(newCommentsCount);

                                // Save the post with the updated comments count
                                return postRepo.save(post)
                                        .then(commentService.commentPost(comment, postId));
                            });
                });
    }

    @Transactional
    public Mono<CommentForm> updateComment(CommentForm commentForm, Integer postId, Integer commentIdForPost) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> {
                    Comment comment = commentFormToComment(commentForm);
                    comment.setAuthorName(user.getUsername());
                    comment.setLastModifiedBy(user.getUsername());
                    comment.setCommentIdForPost(commentIdForPost);
                    return commentService.updateComment(postId, commentIdForPost, user.getUsername(), commentForm.getContent())
                            .map(c -> {
                                BeanUtils.copyProperties(c, commentForm); // Update the commentForm with the updated comment
                                return commentForm;
                            });
                });
    }


    public Flux<CurrentUserCommentDto> getMyComments(Pageable pageable) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMapMany(commentService::findByAuthorName)
                .map(this::CommentToCurrentUserCommentDto);
    }

    public Mono<Boolean> deleteComment(Integer postId, Integer commentIdForPost) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> commentService.deleteComment(postId, commentIdForPost, user.getUsername()))
                .defaultIfEmpty(false); // Comment not found
    }

    public Mono<Boolean> deleteCommentByModerator(Integer postId, Integer commentIdForPost) {
        return commentService.deleteCommentByModerator(postId, commentIdForPost)
                .defaultIfEmpty(false);

    }

    public Flux<CommentShowDto> findAllByPostId(Integer postId, Pageable pageable) {
        return commentService.findAllByPostId(postId, pageable)
                .map(this::CommentToCommentShowDto);
    }

    public Flux<CommentShowDto> findAllByPostIdAndAuthorName(Integer postId, String authorName, Pageable pageable) {
        return commentService.findAllByPostIdAndAuthorName(postId, authorName, pageable)
                .map(this::CommentToCommentShowDto);
    }

    private Comment commentFormToComment(CommentForm commentForm) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentForm, comment); // Swap the arguments to copy from commentForm to comment
        return comment;
    }

    private CurrentUserCommentDto CommentToCurrentUserCommentDto(Comment comment) {
        CurrentUserCommentDto currentUserPostDto = new CurrentUserCommentDto();
        BeanUtils.copyProperties(comment, currentUserPostDto);
        return currentUserPostDto;
    }

    private CommentShowDto CommentToCommentShowDto(Comment comment) {
        CommentShowDto commentShowDto = new CommentShowDto();
        BeanUtils.copyProperties(comment, commentShowDto);
        return commentShowDto;
    }

}
