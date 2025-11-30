package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.post.Repost;
import com.example.weuniteauth.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepostRepository extends JpaRepository<Repost, Long> {
    Optional<Repost> findByUserAndPost(User user, Post post);

    @Query("SELECT r FROM Repost r WHERE r.post.deleted = false")
    List<Repost> findAllActiveReposts();
}
