package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.deleted = false ORDER BY c.createdAt ASC")
    List<Comment> findByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.deleted = false ORDER BY COALESCE(c.updatedAt, c.createdAt) DESC")
    List<Comment> findByUserId(Long userId);

}




