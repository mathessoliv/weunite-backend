package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.Comment;
import com.example.weuniteauth.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);
}
