package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.Like;
import com.example.weuniteauth.domain.Post;
import com.example.weuniteauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User liker, Post liked);

}
