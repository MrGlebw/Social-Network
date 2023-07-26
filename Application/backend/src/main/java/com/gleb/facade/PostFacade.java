package com.gleb.facade;

import com.gleb.data.Post;
import com.gleb.dto.PostForm;
import com.gleb.service.post.PostService;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;
    private final UserService userService;


    public Mono<Object> createPost(PostForm postForm) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> {
                    Post post = new Post();
                    post.setTitle(postForm.getTitle());
                    post.setContent(postForm.getContent());
                    post.setAuthorName(user.getUsername());

                    // Get the post count for the user and set the postIdForUser
                    return postService.getPostsCountByAuthor(user.getUsername())
                            .map(postCount -> {
                                post.setPostIdForUser(postCount + 1);
                                return post;
                            })
                            .flatMap(postService::createPost)
                            .publishOn(Schedulers.boundedElastic())
                            .doOnSuccess(savedPost -> {
                                // Update the post count for the user
                                userService.updatePostCountForUser(user.getUsername())
                                        .subscribe();
                                user.setPostsCount(post.getPostIdForUser());
                                userService.save(user).subscribe();
                            });
                });
    }


    public Flux<Post> getAllPosts() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMapMany(user -> postService.getAllPostsByAuthor(user.getUsername()));
    }

    public Mono<Post> publishPost(Integer postIdForUser) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> postService.publishPost(postIdForUser, user.getUsername()));
    }
}









