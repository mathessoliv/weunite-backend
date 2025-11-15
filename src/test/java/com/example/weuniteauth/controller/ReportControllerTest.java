package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportRequestDTO;
import com.example.weuniteauth.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private ReportDTO reportDTO;

    @BeforeEach
    void setUp() {
        UserDTO reporter = new UserDTO("1", "Reporter", "rep", "BASIC", null,
                "rep@test.com", null, null, false, Instant.now(), Instant.now());
        reportDTO = new ReportDTO("1", reporter, "POST", 2L, "reason", "OPEN", Instant.now());
    }

    @Test
    void shouldCreateReport() {
        ResponseDTO<ReportDTO> response = new ResponseDTO<>("created", reportDTO);
        when(reportService.createReport(anyLong(), any(ReportRequestDTO.class))).thenReturn(response);

        ResponseEntity<ResponseDTO<ReportDTO>> entity = reportController.createReport(1L,
                new ReportRequestDTO("POST", 2L, "reason"));
        assertThat(entity.getStatusCodeValue()).isEqualTo(201);
        assertThat(entity.getBody()).isEqualTo(response);
    }

    @Test
    void shouldListReports() {
        when(reportService.getAllPendingReports()).thenReturn(List.of(reportDTO));
        when(reportService.getAllReports()).thenReturn(List.of(reportDTO));
        when(reportService.getAllReportsByStatus("OPEN")).thenReturn(List.of(reportDTO));
        when(reportService.getReportCount(2L, "POST")).thenReturn(3L);

        assertThat(reportController.getAllPendingReports().getBody()).containsExactly(reportDTO);
        assertThat(reportController.getAllReports().getBody()).containsExactly(reportDTO);
        assertThat(reportController.getAllReportsByStatus("OPEN").getBody()).containsExactly(reportDTO);
        assertThat(reportController.getReportCount(2L, "POST").getBody()).isEqualTo(3L);
    }
}

