package com.example.weuniteauth.service.report;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.mapper.ReportMapper;
import com.example.weuniteauth.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pela consulta de denúncias.
 * Lida com a busca e filtragem de denúncias por diferentes critérios.
 */
@Service
public class ReportQueryService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportQueryService(ReportRepository reportRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    /**
     * Busca todas as denúncias pendentes
     */
    @Transactional(readOnly = true)
    public List<ReportDTO> getAllPendingReports() {
        List<Report> reports = reportRepository.findAllPendingReports();
        return reportMapper.toReportDTOList(reports);
    }

    /**
     * Busca todas as denúncias
     */
    @Transactional(readOnly = true)
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAllReports();
        return reportMapper.toReportDTOList(reports);
    }

    /**
     * Busca denúncias por status específico
     */
    @Transactional(readOnly = true)
    public List<ReportDTO> getAllReportsByStatus(String status) {
        Report.ReportStatus reportStatus = Report.ReportStatus.valueOf(status.toUpperCase());
        List<Report> reports = reportRepository.findAllReportsByStatus(reportStatus);
        return reportMapper.toReportDTOList(reports);
    }

    /**
     * Conta denúncias pendentes para uma entidade específica
     */
    @Transactional(readOnly = true)
    public Long getReportCount(Long entityId, String type) {
        Report.ReportType reportType = Report.ReportType.valueOf(type.toUpperCase());
        return reportRepository.countByEntityIdAndTypeAndStatus(
                entityId,
                reportType,
                Report.ReportStatus.PENDING
        );
    }
}

