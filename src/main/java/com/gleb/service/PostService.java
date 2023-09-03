package com.gleb.service;

import com.gleb.data.post.Post;
import com.gleb.data.post.Status;
import com.gleb.repo.PostRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepo postRepo;

    @CacheEvict(cacheNames = "posts", allEntries = true)
    public Mono<Post> createPost(Post post) {
        return postRepo.save(
                post.toBuilder()
                        .status(Status.DRAFT)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
        ).doOnSuccess(p -> log.info("IN createPost - post: {} created", p));
    }



    public Mono<Integer> getPostsCountByAuthor(String authorName) {
        Flux<Post> allPostsFlux = postRepo.allPostsByAuthorName(authorName);

        return allPostsFlux.collectList()
                .map(List::size);
    }


    @CacheEvict(cacheNames = "posts", allEntries = true)
    public Mono<Post> publishPost(Integer postIdForUser, String authorName) {
        return postRepo.allPostsByAuthorName(authorName)
                .filter(post -> post.getPostIdForUser().equals(postIdForUser))
                .filter(post -> post.getAuthorName().equals(authorName))
                .single()
                .flatMap(post -> postRepo.save(
                        post.toBuilder()
                                .status(Status.PUBLISHED)
                                .publishedDate(LocalDateTime.now())
                                .build()
                ));
    }

    @Cacheable(cacheNames = "publishedPosts", key = "#authorName")
    public Flux<Post> getAllPublishedPostsByAuthor(String authorName, Pageable pageable) {
        return postRepo.allPostsByAuthorNameAndStatus(authorName, Status.PUBLISHED, pageable);
    }


    public Flux<Post> getAllPostsByAuthor(String authorName, Pageable pageable) {
        return postRepo.allPostsByAuthorName(authorName);
    }


    @CacheEvict(cacheNames = "posts", allEntries = true)
    public Mono<Post> updatePost(Integer postIdForUser, String title, String content, String username) {
        return postRepo.allPostsByAuthorName(username)
                .filter(post -> post.getPostIdForUser().equals(postIdForUser))
                .filter(post -> post.getAuthorName().equals(username))
                .single()
                .flatMap(post -> postRepo.save(
                        post.toBuilder()
                                .title(title)
                                .content(content)
                                .lastModifiedDate(LocalDateTime.now())
                                .build()
                ));
    }

    @Caching(evict = { @CacheEvict(cacheNames = "post", key = "#authorName"),
            @CacheEvict(cacheNames = "posts", allEntries = true) })
    public Mono<Boolean> deleteByPostIdForUser(Integer postIdForUser, String authorName) {
        return postRepo.deleteByPostIdForUserAndAuthorName(postIdForUser, authorName);
    }


    @Caching(evict = { @CacheEvict(cacheNames = "post", key = "#postId"),
            @CacheEvict(cacheNames = "posts", allEntries = true) })
    public Mono<Void> deleteByPostId(Integer postId) {
        return postRepo.deleteById(postId);
    }


    @Cacheable(cacheNames = "posts" , key =  "title")
    public Flux<Post> findByTitleContains(String title, Pageable pageable) {
        return postRepo.findByTitleContains(title, pageable)
                .filter(post -> post.getStatus().equals(Status.PUBLISHED));
    }


    @Cacheable(cacheNames = "posts")
    public Flux<Post> getFeed(Pageable pageable) {
        return postRepo.findByStatus(Status.PUBLISHED, pageable);
    }

    @CacheEvict(cacheNames = "posts", allEntries = true)
    public Mono<Void> disapprovePost(Integer postId) {
        return postRepo.findById(postId)
                .flatMap(post -> postRepo.save(
                        post.toBuilder()
                                .status(Status.DISAPPROVED)
                                .disapprovedDate(LocalDateTime.now())
                                .build()
                )).then();
    }

    @Cacheable(cacheNames = "post", key = "#postId", unless = "#result == null")
    public Mono<Post> findById(Integer postId) {
        return postRepo.findById(postId);
    }



}
