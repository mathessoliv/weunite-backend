package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.opportunity.SavedOpportunity;
import com.example.weuniteauth.dto.SavedOpportunityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OpportunityMapper.class})
public interface SavedOpportunityMapper {

    @Mapping(source = "athlete.id", target = "athleteId")
    @Mapping(source = "opportunity", target = "opportunity")
    SavedOpportunityDTO toDTO(SavedOpportunity savedOpportunity);
}

