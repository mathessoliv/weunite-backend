package com.example.weuniteauth.dto.admin;

public record PreviousMonthStatsDTO(
        Long totalPosts,
        Long totalOpportunities,
        Long activeUsers,
        Double engagementRate
) {
}
