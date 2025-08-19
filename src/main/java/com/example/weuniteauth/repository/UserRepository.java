package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
<<<<<<< HEAD
=======

>>>>>>> 6c082badf9e90fdadbe08eb3b6abe9c6a024a0d7
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    boolean existsByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

<<<<<<< HEAD
    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))) AND u.emailVerified = true")
    List<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseAndEmailVerifiedTrue(
            @Param("query") String query, Pageable pageable);
    }
=======
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) AND u.emailVerified = true")
    List<User> findByNameContainingIgnoreCaseAndEmailVerifiedTrue(
            @Param("name") String name, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND u.emailVerified = true")
    List<User> searchUsers(@Param("query") String query);
}
>>>>>>> 6c082badf9e90fdadbe08eb3b6abe9c6a024a0d7
