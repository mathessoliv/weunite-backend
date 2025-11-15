package com.example.weuniteauth.service.admin;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.admin.BanUserRequestDTO;
import com.example.weuniteauth.dto.admin.SuspendUserRequestDTO;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.repository.ReportRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminModerationService Tests")
class AdminModerationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private AdminModerationService adminModerationService;

    private User testUser;
    private Report testReport;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setIsBanned(false);
        testUser.setIsSuspended(false);

        testReport = new Report();
        testReport.setId(1L);
        testReport.setEntityId(1L);
        testReport.setType(Report.ReportType.POST);
        testReport.setStatus(Report.ReportStatus.PENDING);
        testReport.setReason("Spam");
    }

    // BAN USER TESTS

    @Test
    @DisplayName("Should ban user successfully")
    void banUserSuccess() {
        List<Report> userReports = Arrays.asList(testReport);
        BanUserRequestDTO banRequest = new BanUserRequestDTO(1L, 2L, "Violating community guidelines", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reportRepository.findPendingReportsByUser(testUser)).thenReturn(userReports);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(reportRepository.saveAll(anyList())).thenReturn(userReports);

        ResponseDTO<String> result = adminModerationService.banUser(banRequest);

        assertNotNull(result);
        assertTrue(result.message().contains("banido com sucesso"));
        assertTrue(result.data().contains("@testuser"));
        assertTrue(result.data().contains("1 denúncias"));

        // Verificar que o usuário foi marcado como banido
        assertTrue(testUser.getIsBanned());
        assertNotNull(testUser.getBannedAt());
        assertEquals("Violating community guidelines", testUser.getBannedReason());
        assertEquals(2L, testUser.getBannedByAdminId());

        // Verificar que o report foi resolvido
        assertEquals(Report.ReportStatus.RESOLVED, testReport.getStatus());
        assertEquals(Report.ActionTaken.USER_BANNED, testReport.getActionTaken());
        assertEquals(2L, testReport.getResolvedByAdminId());
        assertNotNull(testReport.getResolvedAt());

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        verify(reportRepository).findPendingReportsByUser(testUser);
        verify(reportRepository).saveAll(userReports);
    }

    @Test
    @DisplayName("Should ban user even with no pending reports")
    void banUserWithNoPendingReports() {
        BanUserRequestDTO banRequest = new BanUserRequestDTO(1L, 2L, "Multiple violations", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reportRepository.findPendingReportsByUser(testUser)).thenReturn(Arrays.asList());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(reportRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        ResponseDTO<String> result = adminModerationService.banUser(banRequest);

        assertNotNull(result);
        assertTrue(result.message().contains("banido com sucesso"));
        assertTrue(result.data().contains("0 denúncias"));

        assertTrue(testUser.getIsBanned());

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when banning non-existent user")
    void banUserNotFound() {
        BanUserRequestDTO banRequest = new BanUserRequestDTO(999L, 2L, "Reason", null);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                adminModerationService.banUser(banRequest)
        );

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should resolve multiple reports when banning user")
    void banUserWithMultipleReports() {
        Report report1 = new Report();
        report1.setId(1L);
        report1.setStatus(Report.ReportStatus.PENDING);

        Report report2 = new Report();
        report2.setId(2L);
        report2.setStatus(Report.ReportStatus.PENDING);

        Report report3 = new Report();
        report3.setId(3L);
        report3.setStatus(Report.ReportStatus.PENDING);

        List<Report> userReports = Arrays.asList(report1, report2, report3);
        BanUserRequestDTO banRequest = new BanUserRequestDTO(1L, 2L, "Spam", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reportRepository.findPendingReportsByUser(testUser)).thenReturn(userReports);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(reportRepository.saveAll(anyList())).thenReturn(userReports);

        ResponseDTO<String> result = adminModerationService.banUser(banRequest);

        assertTrue(result.data().contains("3 denúncias"));

        // Verificar que todos os reports foram resolvidos
        userReports.forEach(report -> {
            assertEquals(Report.ReportStatus.RESOLVED, report.getStatus());
            assertEquals(Report.ActionTaken.USER_BANNED, report.getActionTaken());
        });

        verify(reportRepository).saveAll(userReports);
    }

    // SUSPEND USER TESTS

    @Test
    @DisplayName("Should suspend user successfully for specific duration")
    void suspendUserSuccess() {
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(1L, 2L, 7, "Inappropriate behavior", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        ResponseDTO<String> result = adminModerationService.suspendUser(suspendRequest);

        assertNotNull(result);
        assertTrue(result.message().contains("suspenso com sucesso"));
        assertTrue(result.data().contains("@testuser"));
        assertTrue(result.data().contains("7 dia(s)"));

        // Verificar que o usuário foi suspenso
        assertTrue(testUser.getIsSuspended());
        assertNotNull(testUser.getSuspendedUntil());
        assertEquals("Inappropriate behavior", testUser.getSuspensionReason());

        // Verificar que o report foi resolvido
        assertEquals(Report.ReportStatus.RESOLVED, testReport.getStatus());
        assertEquals(Report.ActionTaken.USER_SUSPENDED, testReport.getActionTaken());

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        verify(reportRepository).findById(1L);
        verify(reportRepository).save(testReport);
    }

    @Test
    @DisplayName("Should suspend user without resolving report when reportId is null")
    void suspendUserWithoutReportId() {
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(1L, 2L, 3, "Warning", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseDTO<String> result = adminModerationService.suspendUser(suspendRequest);

        assertNotNull(result);
        assertTrue(result.message().contains("suspenso com sucesso"));

        assertTrue(testUser.getIsSuspended());
        assertNotNull(testUser.getSuspendedUntil());

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        verify(reportRepository, never()).findById(anyLong());
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when suspending non-existent user")
    void suspendUserNotFound() {
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(999L, 2L, 7, "Reason", null);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                adminModerationService.suspendUser(suspendRequest)
        );

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle suspend when report does not exist")
    void suspendUserReportNotFound() {
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(1L, 2L, 7, "Reason", 999L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseDTO<String> result = adminModerationService.suspendUser(suspendRequest);

        assertNotNull(result);
        assertTrue(testUser.getIsSuspended());

        verify(userRepository).findById(1L);
        verify(reportRepository).findById(999L);
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("Should calculate correct suspension end date")
    void suspendUserCorrectEndDate() {
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(1L, 2L, 30, "Long suspension", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Instant before = Instant.now();
        adminModerationService.suspendUser(suspendRequest);
        Instant after = Instant.now();

        assertNotNull(testUser.getSuspendedUntil());

        // Verificar que a data de término está aproximadamente 30 dias no futuro
        // (com margem de alguns segundos para execução do teste)
        Instant expectedEnd = before.plusSeconds(30L * 24L * 60L * 60L);
        assertTrue(testUser.getSuspendedUntil().isAfter(expectedEnd.minusSeconds(10)));
        assertTrue(testUser.getSuspendedUntil().isBefore(after.plusSeconds(30L * 24L * 60L * 60L + 10)));
    }
}
