package com.example.weuniteauth.dto;

import com.example.weuniteauth.domain.Skills;
import com.example.weuniteauth.domain.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record OpportunityDTO(
        Long id,
        String title,
        String description,
        String location,
        LocalDate dateEnd,
        Set<Skills> skills,
        Instant createdAt,
        Instant updatedAt
) {
}
