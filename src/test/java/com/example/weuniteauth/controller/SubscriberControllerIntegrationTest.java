package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.Opportunity.SubscriberDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.service.SubscribersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class SubscriberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscribersService subscribersService;

    private SubscriberDTO sampleSubscriber() {
        UserDTO athleteDTO = new UserDTO(
                "1",
                "Athlete",
                "athlete_user",
                "BASIC",
                null,
                "athlete@test.com",
                null,
                null,
                false,
                Instant.now(),
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                List.of()
        );

        OpportunityDTO opportunityDTO = new OpportunityDTO(
                1L,
                "Sample Opportunity",
                "Description",
                "Remote",
                null,
                Set.of(),
                Instant.now(),
                Instant.now(),
                athleteDTO,
                0
        );

        return new SubscriberDTO(1L, athleteDTO, opportunityDTO);
    }

    @Test
    void toggleSubscriberShouldReturnResponse() throws Exception {
        ResponseDTO<SubscriberDTO> response = new ResponseDTO<>("toggled", sampleSubscriber());
        when(subscribersService.toggleSubscriber(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/subscriber/toggleSubscriber/{athleteId}/{opportunityId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("toggled"));
    }

    @Test
    void getSubscribersByOpportunityShouldReturnList() throws Exception {
        when(subscribersService.getSubscribersByOpportunity(2L)).thenReturn(List.of(sampleSubscriber()));

        mockMvc.perform(get("/api/subscriber/subscribers/{opportunityId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}

