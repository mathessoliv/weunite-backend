package com.example.weuniteauth.service.report;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportRequestDTO;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.ReportMapper;
import com.example.weuniteauth.repository.ReportRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pela criação de denúncias.
 * Lida com o registro de novas denúncias de usuários.
 */
@Service
public class ReportCreationService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    public ReportCreationService(ReportRepository reportRepository,
                                 UserRepository userRepository,
                                 ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportMapper = reportMapper;
    }

    /**
     * Cria uma nova denúncia para um conteúdo específico
     */
    @Transactional
    public ResponseDTO<ReportDTO> createReport(Long userId, ReportRequestDTO reportRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Report.ReportType type = Report.ReportType.valueOf(reportRequestDTO.type().toUpperCase());

        Report report = new Report(
                user,
                type,
                reportRequestDTO.entityId(),
                reportRequestDTO.reason()
        );

        reportRepository.save(report);

        return reportMapper.toResponseDTO("Denúncia registrada com sucesso!", report);
    }
}

