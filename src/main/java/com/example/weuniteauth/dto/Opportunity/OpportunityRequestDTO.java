package com.example.weuniteauth.dto.Opportunity;

import com.example.weuniteauth.domain.Skills;
import com.example.weuniteauth.validations.ValidOpportunity;

import java.time.LocalDate;
import java.util.Set;


@ValidOpportunity
public record OpportunityRequestDTO(
        String title,
        String description,
        String location,
        LocalDate dateEnd,
        Set<Skills> skills

) {

}
