package com.gleb.facade;

import com.gleb.data.post.Post;
import com.gleb.dto.post.CurrentUserPostDto;
import com.gleb.dto.post.PostForm;
import com.gleb.dto.post.PostShowDto;
import com.gleb.service.post.PostService;
import com.gleb.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
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



    public Mono<Post> publishPost(Integer postIdForUser) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> postService.publishPost(postIdForUser, user.getUsername()));
    }

    public Flux <CurrentUserPostDto> getAllPublishedPosts () {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMapMany(user -> postService.getAllPublishedPostsByAuthor(user.getUsername()))
                .map(this::postToCurrentUserPostDto);
    }

    public Flux <CurrentUserPostDto> getAllUnpublishedPosts () {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMapMany(user -> postService.getAllUnpublishedPostsByAuthor(user.getUsername()))
                .map(this::postToCurrentUserPostDto);
    }

    public Mono<PostForm> updatePost(Integer postIdForUser, PostForm postForm) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> postService.updatePost(postIdForUser, postForm.getTitle(), postForm.getContent(), user.getUsername()))
                .map(post -> {
                    BeanUtils.copyProperties(postForm , post);
                    return postForm;
                });
    }

    public Mono <Void> deleteMyPost (Integer postIdForUser) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> postService.deleteByPostIdForUser(postIdForUser, user.getUsername()));
    }


    private PostShowDto postToShowDto (Post post){
        PostShowDto postShowDto = new PostShowDto();
        BeanUtils.copyProperties(post, postShowDto);
        return postShowDto;
    }

    public Flux <PostShowDto> findByTitleContains (String q , Pageable pageable) {
        return postService.findByTitleContains(q , pageable)
                .map(this::postToShowDto);
    }

    public Flux <PostShowDto> getFeed(Pageable pageable) {
        return postService.getFeed(pageable)
                .map(this::postToShowDto);
    }

    public Mono<Boolean> deletePost(Integer id) {
        return postService.deleteByPostId(id)
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    public Mono <Boolean> disapprovePost (Integer postId) {
        return postService.disapprovePost(postId)
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    private CurrentUserPostDto postToCurrentUserPostDto (Post post){
        CurrentUserPostDto currentUserPostDto = new CurrentUserPostDto();
        BeanUtils.copyProperties(post, currentUserPostDto);
        return currentUserPostDto;
    }




}









