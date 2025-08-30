package com.example.weuniteauth.dto;

import com.example.weuniteauth.domain.User;

import java.time.LocalDate;
import java.util.Set;

public record OpportunityDTO(
        String title,
        String description,
        String location,
        LocalDate dateEnd,
        Set<User> skills
) {
}
