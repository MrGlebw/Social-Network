package com.gleb.web.post;

import com.gleb.data.Post;
import com.gleb.dto.post.PostShowDto;
import com.gleb.facade.PostFacade;
import com.gleb.repo.CommentRepo;
import com.gleb.repo.PostRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController()
@RequestMapping(value = "/feed")
@RequiredArgsConstructor
@Validated
public class PostController {


    private final CommentRepo commentRepo;

    private final PostFacade postFacade;

    @GetMapping("")
    public Flux<PostShowDto> all(@RequestParam(value = "q", required = false) String q,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

        if (StringUtils.hasText(q)) {
            return postFacade.findByTitleContains(q, PageRequest.of(page, size, sort));
        }
        else {
            return postFacade.getFeed().skip(page).take(size);
        }
    }


}

