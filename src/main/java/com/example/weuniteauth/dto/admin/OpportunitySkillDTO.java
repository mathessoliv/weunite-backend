package com.example.weuniteauth.dto.admin;

/**
 * DTO para representar uma skill com sua contagem de ocorrências
 */
public record OpportunitySkillDTO(
        String name,
        Long count
) {
}
