package com.gleb.facade;

import com.gleb.data.Post.Post;
import com.gleb.dto.PostDTO;
import com.gleb.service.PostService;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PostFacade {
    private final PostService postService;
    public PostFacade(PostService postService) {
        this.postService = postService;
    }

   private Post PostDTOtoPost (PostDTO postDTO) {
        Post post = new Post();
        BeanUtils.copyProperties(postDTO, post);
        return post;
    }

    private PostDTO PosttoPostDTO (Post post) {
        PostDTO postDTO = new PostDTO();
        BeanUtils.copyProperties(post, postDTO);
        return postDTO;
    }
    public Mono<PostDTO> createPost(PostDTO postDTO) {
        Post post = PostDTOtoPost(postDTO);
        return postService.createPost(post)
                .map(this::PosttoPostDTO);
    }

    public Mono<PostDTO> getPostById(Long id) {
        return postService.getPostById(id)
                .map(this::PosttoPostDTO);
    }

    public Flux<PostDTO> getAllPosts() {
        return postService.getAllPosts()
                .map(this::PosttoPostDTO);
    }

    public Flux<PostDTO> getPostsByUserId(Long id) {
        return postService.getPostsByUserId(id)
                .map(this::PosttoPostDTO);
    }

    public Mono <Void> deletePostById(Long id) {
        return postService.deletePostById(id);
    }

}
