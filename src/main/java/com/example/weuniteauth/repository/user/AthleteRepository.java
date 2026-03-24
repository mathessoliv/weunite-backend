package com.example.weuniteauth.repository.user;

import com.example.weuniteauth.domain.users.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    Optional<Athlete> findByUsername(String username);
}
