package com.gleb.facade;

import com.gleb.data.Post;
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

    public Flux <Post> getAllPublishedPosts () {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMapMany(user -> postService.getAllPublishedPostsByAuthor(user.getUsername()));
    }

    public Flux <Post> getAllUnpublishedPosts () {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMapMany(user -> postService.getAllUnpublishedPostsByAuthor(user.getUsername()));
    }

    public Mono<PostForm> updatePost(Integer postIdForUser, PostForm postForm) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> postService.updatePost(postIdForUser, postForm.getTitle(), postForm.getContent(), user.getUsername()))
                .map(post -> {
                    BeanUtils.copyProperties(post, postForm);
                    return postForm;
                });
    }

    public Mono <Void> deletePost (Integer postIdForUser) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(userService::findUserByUsername)
                .flatMap(user -> postService.deleteByPostIdForUser(postIdForUser, user.getUsername()));
    }

    public Flux <PostShowDto> getFeed () {
        return postService.getFeed()
                .map(this::postToShowDto);
    }

    public PostShowDto postToShowDto (Post post){
        PostShowDto postShowDto = new PostShowDto();
        BeanUtils.copyProperties(post, postShowDto);
        return postShowDto;
    }

    public Flux <PostShowDto> findByTitleContains (String q , Pageable pageable) {
        return postService.findByTitleContains(q , pageable)
                .map(this::postToShowDto);
    }




}









