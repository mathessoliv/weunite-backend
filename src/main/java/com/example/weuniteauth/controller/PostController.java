package com.example.weuniteauth.controller;


import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.post.PostRequestDTO;
import com.example.weuniteauth.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<ResponseDTO<PostDTO>> createPost(@PathVariable Long userId, @RequestBody @Valid PostRequestDTO post) {
        ResponseDTO<PostDTO> createdPost = postService.createPost(userId, post);
        return ResponseEntity.status(HttpStatus.OK).body(createdPost);
    }

    @PutMapping("/update/{userId}/{postId}")
    public ResponseEntity<ResponseDTO<PostDTO>> updatePost(@PathVariable Long userId, @PathVariable Long postId, @RequestBody @Valid PostRequestDTO post) {
        ResponseDTO<PostDTO> updatedPost = postService.updatePost(userId, postId, post);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPost);
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<ResponseDTO<PostDTO>> getPost(@PathVariable Long postId) {
        ResponseDTO<PostDTO> post = postService.getPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @DeleteMapping("/delete/{userId}/{postId}")
    public ResponseEntity<ResponseDTO<PostDTO>> deletePost(@PathVariable Long userId, @PathVariable Long postId) {
        ResponseDTO<PostDTO> post = postService.deletePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
}
