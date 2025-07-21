package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.CommentDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.comment.CommentRequestDTO;
import com.example.weuniteauth.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<CommentDTO>> createComment(
            @RequestParam Long userId,
            @RequestParam Long postId,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        ResponseDTO<CommentDTO> commentDTO = commentService.createComment(userId, postId, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(commentDTO);
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

}
