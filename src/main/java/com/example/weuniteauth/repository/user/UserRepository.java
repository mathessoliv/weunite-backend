
package com.example.weuniteauth.repository.user;

import com.example.weuniteauth.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.emailVerified = true")
    List<User> findByNameContainingIgnoreCaseAndEmailVerifiedTrue(
            @Param("name") String name, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND u.emailVerified = true")
    List<User> searchUsers(@Param("query") String query);

    @Query("SELECT COUNT(DISTINCT p.user.id) FROM Post p WHERE p.createdAt >= :startDate")
    Long countActiveUsersByPostActivity(@Param("startDate") Instant startDate);

    @Query("SELECT COUNT(u) FROM User u WHERE TYPE(u) = com.example.weuniteauth.domain.users.Athlete")
    Long countAthletes();

    @Query("SELECT COUNT(u) FROM User u WHERE TYPE(u) = com.example.weuniteauth.domain.users.Company")
    Long countCompanies();
}
