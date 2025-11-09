package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p ORDER BY COALESCE(p.updatedAt, p.createdAt) DESC")
    List<Post> findAllOrderedByCreationDate();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt >= :startDate AND p.createdAt < :endDate")
    Long countPostsBetweenDates(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT FUNCTION('MONTH', p.createdAt) as month, COUNT(p) FROM Post p WHERE p.createdAt >= :startDate GROUP BY FUNCTION('MONTH', p.createdAt) ORDER BY month")
    List<Object[]> countPostsByMonth(@Param("startDate") Instant startDate);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post IS NOT NULL")
    Long countTotalLikes();

    @Query("SELECT COUNT(c) FROM Comment c")
    Long countTotalComments();

}
