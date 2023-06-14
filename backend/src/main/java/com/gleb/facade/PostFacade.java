package com.gleb.facade;


import com.gleb.data.Post;
import com.gleb.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PostFacade {
    private final PostService postService;
    public PostFacade(PostService postService) {
        this.postService = postService;
    }
       public Mono<ResponseEntity<Post>> createPost(Post post) {
            return postService.createPost(post)
                    .map(ResponseEntity::ok);
        }
        public Flux<ResponseEntity<Post>> getPost(Long id) {
            return postService.findAllByUserId(id)
                    .map(ResponseEntity::ok);
        }

        public Mono<ResponseEntity<Post>> updatePost(Post post) {
            return postService.updatePost(post)
                    .map(ResponseEntity::ok);
        }

        public Mono<Void> deletePost(Long id) {
            return postService.deletePost(id);
        }



}
