package com.example.weuniteauth.dto.admin;

public record MonthlyDataDTO(
        String month,
        Long posts,
        Long opportunities
) {
}
