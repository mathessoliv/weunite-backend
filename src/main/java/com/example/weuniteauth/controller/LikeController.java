package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.LikeDTO;
import com.example.weuniteauth.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggleLike/{userId}/{postId}")
    public ResponseEntity<LikeDTO> toggleLike(@PathVariable Long userId, @PathVariable Long postId) {
        LikeDTO result = likeService.toggleLike(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<Set<LikeDTO>> getLikes(@PathVariable Long userId) {
        Set<LikeDTO> result = likeService.getLikes(userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/get/{userId}/page")
    public ResponseEntity<Set<LikeDTO>> getLikes(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        Set<LikeDTO> result = likeService.getLikes(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
