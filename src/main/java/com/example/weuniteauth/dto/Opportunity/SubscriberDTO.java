package com.example.weuniteauth.dto.Opportunity;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.UserDTO;

public record SubscriberDTO(
        Long id,
        UserDTO athlete,
        OpportunityDTO opportunity
) {
}

