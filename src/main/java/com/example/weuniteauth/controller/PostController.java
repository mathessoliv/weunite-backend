package com.example.weuniteauth.controller;


import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.post.CreatePostRequestDTO;
import com.example.weuniteauth.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody @Valid CreatePostRequestDTO post) {
        PostDTO createdPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.OK).body(createdPost);
    }

}
