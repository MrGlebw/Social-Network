package com.gleb.service.post;

import com.gleb.data.post.Post;
import com.gleb.data.post.Status;
import com.gleb.repo.PostRepo;
import com.gleb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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



    public Mono<Integer> getPostsCountByAuthor(String authorName) {
        Flux<Post> allPostsFlux = postRepo.allPostsByAuthorName(authorName);

        return allPostsFlux.collectList()
                .map(List::size);
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

    public Flux <Post> getAllPublishedPostsByAuthor(String authorName) {
        return postRepo.allPostsByAuthorName(authorName)
                .filter(post -> post.getStatus().equals(Status.PUBLISHED));
    }

    public Flux <Post> getAllUnpublishedPostsByAuthor(String authorName) {
        return postRepo.allPostsByAuthorName(authorName)
                .filter(post -> post.getStatus().equals(Status.DISAPPROVED) | post.getStatus().equals(Status.DRAFT));
    }




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

    public Mono<Void> deleteByPostIdForUser (Integer postIdForUser , String authorName) {
        return postRepo.deleteByPostIdForUserAndAuthorName(postIdForUser , authorName);
    }

    public Mono <Void> deleteByPostId (Integer postId) {
        return postRepo.deleteById(postId);
    }


    public Flux <Post> findByTitleContains (String title, Pageable pageable) {
        return postRepo.findByTitleContains(title , pageable)
                .filter(post -> post.getStatus().equals(Status.PUBLISHED));
    }

    public Flux<Post> getFeed (Pageable pageable) {
        return postRepo.findByStatus(Status.PUBLISHED, pageable);
    }

    public Mono <Void> disapprovePost (Integer postId){
        return postRepo.findById(postId)
                .flatMap(post -> postRepo.save(
                        post.toBuilder()
                                .status(Status.DISAPPROVED)
                                .build()
                )).then();
    }

}
