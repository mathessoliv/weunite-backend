package com.example.weuniteauth.dto;

import java.time.Instant;

public record SavedOpportunityDTO(
        Long id,
        Long athleteId,
        OpportunityDTO opportunity,
        Instant savedAt
) {
}

