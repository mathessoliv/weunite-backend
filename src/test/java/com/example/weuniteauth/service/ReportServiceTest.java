package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportRequestDTO;
import com.example.weuniteauth.service.report.ReportCreationService;
import com.example.weuniteauth.service.report.ReportQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportCreationService reportCreationService;

    @Mock
    private ReportQueryService reportQueryService;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("createReport deve delegar para ReportCreationService")
    void createReport_deveDelegarParaServicoDeCriacao() {
        Long userId = 1L;
        ReportRequestDTO requestDTO = new ReportRequestDTO("POST", 10L, "SPAM");
        UserDTO reporter = mock(UserDTO.class);
        ReportDTO reportDTO = new ReportDTO("id-1", reporter, "POST", 10L, "SPAM", "PENDING", Instant.now(), null, null);
        ResponseDTO<ReportDTO> responseDTO = new ResponseDTO<>("ok", reportDTO);

        when(reportCreationService.createReport(userId, requestDTO)).thenReturn(responseDTO);

        ResponseDTO<ReportDTO> result = reportService.createReport(userId, requestDTO);

        assertSame(responseDTO, result);
        verify(reportCreationService).createReport(userId, requestDTO);
    }

    @Test
    @DisplayName("getAllPendingReports deve delegar para ReportQueryService")
    void getAllPendingReports_deveDelegarParaServicoDeConsulta() {
        ReportDTO dto = new ReportDTO("id-1", mock(UserDTO.class), "POST", 10L, "SPAM", "PENDING", Instant.now(), null, null);
        List<ReportDTO> list = List.of(dto);
        when(reportQueryService.getAllPendingReports()).thenReturn(list);

        List<ReportDTO> result = reportService.getAllPendingReports();

        assertSame(list, result);
        verify(reportQueryService).getAllPendingReports();
    }

    @Test
    @DisplayName("getAllReports deve delegar para ReportQueryService")
    void getAllReports_deveDelegarParaServicoDeConsulta() {
        ReportDTO dto = new ReportDTO("id-1", mock(UserDTO.class), "POST", 10L, "SPAM", "PENDING", Instant.now(), null, null);
        List<ReportDTO> list = List.of(dto);
        when(reportQueryService.getAllReports()).thenReturn(list);

        List<ReportDTO> result = reportService.getAllReports();

        assertSame(list, result);
        verify(reportQueryService).getAllReports();
    }

    @Test
    @DisplayName("getAllReportsByStatus deve delegar para ReportQueryService")
    void getAllReportsByStatus_deveDelegarParaServicoDeConsulta() {
        ReportDTO dto = new ReportDTO("id-1", mock(UserDTO.class), "POST", 10L, "SPAM", "PENDING", Instant.now(), null, null);
        List<ReportDTO> list = List.of(dto);
        when(reportQueryService.getAllReportsByStatus("PENDING")).thenReturn(list);

        List<ReportDTO> result = reportService.getAllReportsByStatus("PENDING");

        assertSame(list, result);
        verify(reportQueryService).getAllReportsByStatus("PENDING");
    }

    @Test
    @DisplayName("getReportCount deve delegar para ReportQueryService")
    void getReportCount_deveDelegarParaServicoDeConsulta() {
        when(reportQueryService.getReportCount(10L, "POST")).thenReturn(5L);

        Long count = reportService.getReportCount(10L, "POST");

        assertEquals(5L, count);
        verify(reportQueryService).getReportCount(10L, "POST");
    }
}
