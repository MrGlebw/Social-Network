package com.gleb.service.post;

import com.gleb.data.Post;
import com.gleb.data.Status;
import com.gleb.repo.PostRepo;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public Mono<Post> createPost(Post post) {
        return postRepo.save(
                post.toBuilder()
                        .status(Status.DRAFT)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build()
        ).doOnSuccess(p -> {
            log.info("IN createPost - post: {} created", p);
        });
    }


    public Flux<Post> getAllPostsByAuthor(String authorName) {
        return postRepo.allPostsByAuthorName(authorName);
    }

    public Mono<Integer> getPostsCountByAuthor(String authorName) {
        Flux<Post> allPostsFlux = postRepo.allPostsByAuthorName(authorName);

        return allPostsFlux.collectList()
                .map(posts -> posts.size());
    }

    public Mono<Post> publishPost(Integer postIdForUser , String authorName) {
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





}
