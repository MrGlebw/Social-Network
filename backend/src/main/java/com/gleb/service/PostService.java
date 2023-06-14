package com.gleb.service;

import com.gleb.data.Post;
import com.gleb.repo.PostRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepo postRepo;


    public PostService(PostRepo postRepo) {
        this.postRepo = postRepo;
    }

    public Flux<Post> findAllByUserId(Long userId) {
        return postRepo.findAllByUserId(userId);
    }


    @Transactional
    public Mono<Post> createPost(Post post) {
        return postRepo.save(post);
    }



   public Mono<Post> updatePost(Post post) {
        return postRepo.save(post);
    }


    @Transactional
   public Mono<Void> deletePost(Long id) {
        return postRepo.deleteById(id);
    }



}


