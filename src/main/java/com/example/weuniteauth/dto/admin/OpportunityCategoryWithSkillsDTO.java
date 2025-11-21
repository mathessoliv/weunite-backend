package com.example.weuniteauth.dto.admin;

import java.util.List;

/**
 * DTO para representar uma categoria de oportunidade com suas skills mais frequentes
 */
public record OpportunityCategoryWithSkillsDTO(
        String category,
        Long count,
        List<OpportunitySkillDTO> topSkills
) {
}
