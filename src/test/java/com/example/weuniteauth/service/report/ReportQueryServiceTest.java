package com.example.weuniteauth.service.report;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.mapper.ReportMapper;
import com.example.weuniteauth.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportQueryService reportQueryService;

    @Test
    @DisplayName("getAllPendingReports deve buscar pendentes e mapear para ReportDTO")
    void getAllPendingReports_sucesso() {
        Report report = mock(Report.class);
        List<Report> reports = List.of(report);
        List<ReportDTO> dtos = List.of(mock(ReportDTO.class));

        when(reportRepository.findAllPendingReports()).thenReturn(reports);
        when(reportMapper.toReportDTOList(reports)).thenReturn(dtos);

        List<ReportDTO> result = reportQueryService.getAllPendingReports();

        assertSame(dtos, result);
        verify(reportRepository).findAllPendingReports();
        verify(reportMapper).toReportDTOList(reports);
    }

    @Test
    @DisplayName("getAllReports deve buscar todas as denuncias e mapear para ReportDTO")
    void getAllReports_sucesso() {
        Report report = mock(Report.class);
        List<Report> reports = List.of(report);
        List<ReportDTO> dtos = List.of(mock(ReportDTO.class));

        when(reportRepository.findAllReports()).thenReturn(reports);
        when(reportMapper.toReportDTOList(reports)).thenReturn(dtos);

        List<ReportDTO> result = reportQueryService.getAllReports();

        assertSame(dtos, result);
        verify(reportRepository).findAllReports();
        verify(reportMapper).toReportDTOList(reports);
    }

    @Test
    @DisplayName("getAllReportsByStatus deve converter status para enum e delegar ao repositorio")
    void getAllReportsByStatus_sucesso() {
        Report report = mock(Report.class);
        List<Report> reports = List.of(report);
        List<ReportDTO> dtos = List.of(mock(ReportDTO.class));

        when(reportRepository.findAllReportsByStatus(Report.ReportStatus.PENDING)).thenReturn(reports);
        when(reportMapper.toReportDTOList(reports)).thenReturn(dtos);

        List<ReportDTO> result = reportQueryService.getAllReportsByStatus("pending");

        assertSame(dtos, result);
        verify(reportRepository).findAllReportsByStatus(Report.ReportStatus.PENDING);
        verify(reportMapper).toReportDTOList(reports);
    }

    @Test
    @DisplayName("getReportCount deve converter type para enum e delegar ao repositorio")
    void getReportCount_sucesso() {
        when(reportRepository.countByEntityIdAndTypeAndStatus(
                10L,
                Report.ReportType.POST,
                Report.ReportStatus.PENDING
        )).thenReturn(5L);

        Long result = reportQueryService.getReportCount(10L, "post");

        assertEquals(5L, result);
        verify(reportRepository).countByEntityIdAndTypeAndStatus(
                10L,
                Report.ReportType.POST,
                Report.ReportStatus.PENDING
        );
    }
}

