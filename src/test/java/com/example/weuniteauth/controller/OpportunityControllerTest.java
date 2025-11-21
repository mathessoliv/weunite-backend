package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.service.OpportunityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpportunityControllerTest {

    @Mock
    private OpportunityService opportunityService;

    @InjectMocks
    private OpportunityController opportunityController;

    private OpportunityDTO dto;
    private ResponseDTO<OpportunityDTO> response;

    @BeforeEach
    void setUp() {
        dto = new OpportunityDTO(1L, "Title", "Desc", "Remote", LocalDate.now(), Set.of(), Instant.now(), Instant.now(), null, 0);
        response = new ResponseDTO<>("ok", dto);
    }

    @Test
    void shouldCreateOpportunity() {
        when(opportunityService.createOpportunity(anyLong(), any())).thenReturn(response);
        ResponseEntity<ResponseDTO<OpportunityDTO>> entity = opportunityController.createOpportunity(1L, null);
        assertThat(entity.getBody()).isEqualTo(response);
    }

    @Test
    void shouldUpdateOpportunity() {
        when(opportunityService.updateOpportunity(anyLong(), anyLong(), any())).thenReturn(response);
        assertThat(opportunityController.updateOpportunity(1L, 2L, dto).getBody()).isEqualTo(response);
    }

    @Test
    void shouldGetOpportunityAndLists() {
        when(opportunityService.getOpportunity(2L)).thenReturn(response);
        when(opportunityService.getOpportunities()).thenReturn(List.of(dto));
        when(opportunityService.getOpportunitiesByCompanyId(3L)).thenReturn(List.of(dto));

        assertThat(opportunityController.getOpportunity(2L).getBody()).isEqualTo(response);
        assertThat(opportunityController.getOpportunities().getBody().data()).containsExactly(dto);
        assertThat(opportunityController.getOpportunitiesByCompanyId(3L).getBody().data()).containsExactly(dto);
    }

    @Test
    void shouldDeleteOpportunity() {
        when(opportunityService.deleteOpportunity(anyLong(), anyLong())).thenReturn(response);
        assertThat(opportunityController.deleteOpportunity(1L, 2L).getBody()).isEqualTo(response);
    }
}

