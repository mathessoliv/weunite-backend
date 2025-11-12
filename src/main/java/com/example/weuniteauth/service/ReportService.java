package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportRequestDTO;
import com.example.weuniteauth.service.report.ReportCreationService;
import com.example.weuniteauth.service.report.ReportQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço facade para operações de denúncias.
 * Delega para serviços especializados:
 * - ReportCreationService: Criação de denúncias
 * - ReportQueryService: Consulta de denúncias
 */
@Service
public class ReportService {

    private final ReportCreationService reportCreationService;
    private final ReportQueryService reportQueryService;

    public ReportService(ReportCreationService reportCreationService, ReportQueryService reportQueryService) {
        this.reportCreationService = reportCreationService;
        this.reportQueryService = reportQueryService;
    }

    // ========== Delegação para ReportCreationService ==========

    public ResponseDTO<ReportDTO> createReport(Long userId, ReportRequestDTO reportRequestDTO) {
        return reportCreationService.createReport(userId, reportRequestDTO);
    }

    // ========== Delegação para ReportQueryService ==========

    public List<ReportDTO> getAllPendingReports() {
        return reportQueryService.getAllPendingReports();
    }

    public List<ReportDTO> getAllReports() {
        return reportQueryService.getAllReports();
    }

    public List<ReportDTO> getAllReportsByStatus(String status) {
        return reportQueryService.getAllReportsByStatus(status);
    }

    public Long getReportCount(Long entityId, String type) {
        return reportQueryService.getReportCount(entityId, type);
    }
}

