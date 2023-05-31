package com.gleb.service;

import com.gleb.data.Post.Post;
import com.gleb.repo.PostRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class PostService {

    private final PostRepo postRepo;


    public PostService(PostRepo postRepo) {
        this.postRepo = postRepo;
    }

    public Mono<Post> createPost(Post post) {
        return postRepo.save(post);
    }

    public Mono<Post> getPostById(Long id) {
        return postRepo.findById(id);
    }

    public Flux<Post> getAllPosts() {
        return postRepo.findAll();
    }

    public Flux<Post> getPostsByUserId(Long id) {
        return postRepo.findByUserId(id);
    }

    public Mono<Void> deletePostById (Long id) {
        return postRepo.deleteById(id);
    }
}


