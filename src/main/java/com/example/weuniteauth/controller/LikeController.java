package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggleLike/{userId}/{postId}")
    public ResponseEntity<LikeDTO> toggleLike(@PathVariable Long userId, @PathVariable Long postId) {
        LikeDTO result = likeService.togglelike(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
