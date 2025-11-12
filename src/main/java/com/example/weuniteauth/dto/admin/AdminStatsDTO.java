package com.example.weuniteauth.dto.admin;

public record AdminStatsDTO(
        Long totalPosts,
        Long totalOpportunities,
        Long activeUsers,
        Double engagementRate,
        PreviousMonthStatsDTO previousMonth
) {
}
