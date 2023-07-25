package com.gleb.web;

import com.gleb.data.Comment;
import com.gleb.data.Post;
import com.gleb.data.PostId;
import com.gleb.dto.CommentForm;
import com.gleb.dto.PostForm;
import com.gleb.exceptions.PostNotFoundException;
import com.gleb.repo.CommentRepo;
import com.gleb.repo.PostRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.created;

@RestController()
@RequestMapping(value = "users//posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostRepo posts;

    private final CommentRepo comments;

    @GetMapping("")
    public Flux<Post> all(@RequestParam(value = "q", required = false) String q,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

        if (StringUtils.hasText(q)) {
            return this.posts.findByTitleContains(q, PageRequest.of(page, size, sort));
        }
        else {
            return this.posts.findAll(sort).skip(page).take(size);
        }
    }


}

