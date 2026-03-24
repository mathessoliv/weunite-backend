package com.example.weuniteauth.service.admin;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.admin.BanUserRequestDTO;
import com.example.weuniteauth.dto.admin.SuspendUserRequestDTO;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.repository.ReportRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Serviço responsável pela moderação de usuários.
 * Lida com suspensões e banimentos de usuários.
 */
@Service
public class AdminModerationService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public AdminModerationService(UserRepository userRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    /**
     * Bane um usuário permanentemente.
     * Fecha TODAS as denúncias relacionadas ao usuário.
     */
    @Transactional
    public ResponseDTO<String> banUser(BanUserRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);

        // Marcar usuário como banido
        user.setIsBanned(true);
        user.setBannedAt(Instant.now());
        user.setBannedReason(request.reason());
        user.setBannedByAdminId(request.adminId());
        userRepository.save(user);

        // Resolver todas as denúncias pendentes do usuário
        List<Report> userReports = reportRepository.findPendingReportsByUser(user);
        Instant now = Instant.now();

        userReports.forEach(report -> {
            report.setStatus(Report.ReportStatus.RESOLVED);
            report.setActionTaken(Report.ActionTaken.USER_BANNED);
            report.setResolvedByAdminId(request.adminId());
            report.setResolvedAt(now);
        });

        reportRepository.saveAll(userReports);

        return new ResponseDTO<>(
                "Usuário banido com sucesso",
                String.format("Usuário @%s foi banido permanentemente. %d denúncias foram resolvidas.",
                        user.getUsername(), userReports.size())
        );
    }

    /**
     * Suspende um usuário temporariamente.
     * Fecha APENAS a denúncia específica (se fornecida).
     */
    @Transactional
    public ResponseDTO<String> suspendUser(SuspendUserRequestDTO request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);

        // Marcar usuário como suspenso
        user.setIsSuspended(true);
        Instant suspendedUntil = Instant.now().plus(request.durationInDays(), ChronoUnit.DAYS);
        user.setSuspendedUntil(suspendedUntil);
        user.setSuspensionReason(request.reason());
        userRepository.save(user);

        // Resolver apenas a denúncia específica (se fornecida)
        if (request.reportId() != null) {
            Report report = reportRepository.findById(request.reportId())
                    .orElse(null);

            if (report != null) {
                report.setStatus(Report.ReportStatus.RESOLVED);
                report.setActionTaken(Report.ActionTaken.USER_SUSPENDED);
                report.setResolvedByAdminId(request.adminId());
                report.setResolvedAt(Instant.now());
                reportRepository.save(report);
            }
        }

        return new ResponseDTO<>(
                "Usuário suspenso com sucesso",
                String.format("Usuário @%s foi suspenso por %d dia(s).",
                        user.getUsername(), request.durationInDays())
        );
    }
}

