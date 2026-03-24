package com.example.weuniteauth.service.admin;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.BanUserRequestDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.SuspendUserRequestDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.dto.report.ReportSummaryDTO;
import com.example.weuniteauth.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Tests")
class AdminServiceTest {

    @Mock
    private AdminStatsService adminStatsService;

    @Mock
    private AdminReportService adminReportService;

    @Mock
    private AdminModerationService adminModerationService;

    @InjectMocks
    private AdminService adminService;

    // ADMIN STATS TESTS

    @Test
    @DisplayName("Should get admin stats successfully")
    void getAdminStatsSuccess() {
        AdminStatsDTO mockStats = mock(AdminStatsDTO.class);
        when(adminStatsService.getAdminStats()).thenReturn(mockStats);

        AdminStatsDTO result = adminService.getAdminStats();

        assertNotNull(result);
        verify(adminStatsService).getAdminStats();
    }

    @Test
    @DisplayName("Should get monthly data successfully")
    void getMonthlyDataSuccess() {
        MonthlyDataDTO monthlyDataDTO = new MonthlyDataDTO("Janeiro", 15L, 8L);
        List<MonthlyDataDTO> monthlyData = Arrays.asList(monthlyDataDTO);

        when(adminStatsService.getMonthlyData()).thenReturn(monthlyData);

        List<MonthlyDataDTO> result = adminService.getMonthlyData();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Janeiro", result.get(0).month());

        verify(adminStatsService).getMonthlyData();
    }

    @Test
    @DisplayName("Should get user type data successfully")
    void getUserTypeDataSuccess() {
        UserTypeDataDTO userTypeDataDTO = new UserTypeDataDTO("ATHLETE", 60L);
        List<UserTypeDataDTO> userTypeData = Arrays.asList(userTypeDataDTO);

        when(adminStatsService.getUserTypeData()).thenReturn(userTypeData);

        List<UserTypeDataDTO> result = adminService.getUserTypeData();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ATHLETE", result.get(0).name());
        assertEquals(60L, result.get(0).value());

        verify(adminStatsService).getUserTypeData();
    }

    // REPORT MANAGEMENT TESTS

    @Test
    @DisplayName("Should get posts with many reports successfully")
    void getPostsWithManyReportsSuccess() {
        ReportSummaryDTO reportSummaryDTO = new ReportSummaryDTO(1L, "POST", 5L);
        List<ReportSummaryDTO> reports = Arrays.asList(reportSummaryDTO);

        when(adminReportService.getPostsWithManyReports()).thenReturn(reports);

        List<ReportSummaryDTO> result = adminService.getPostsWithManyReports();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).entityId());
        assertEquals(5L, result.get(0).reportCount());

        verify(adminReportService).getPostsWithManyReports();
    }

    @Test
    @DisplayName("Should delete post by admin successfully")
    void deletePostByAdminSuccess() {
        PostDTO postDTO = new PostDTO(
                "1",
                "Post content",
                null,
                null,
                Arrays.asList(),
                Arrays.asList(),
                Arrays.asList(),
                Instant.now(),
                null,
                null,
                null,
                null
        );
        ResponseDTO<PostDTO> expectedResponse = new ResponseDTO<>("Post deletado com sucesso", postDTO);

        when(adminReportService.deletePostByAdmin(1L)).thenReturn(expectedResponse);

        ResponseDTO<PostDTO> result = adminService.deletePostByAdmin(1L);

        assertNotNull(result);
        assertEquals("Post deletado com sucesso", result.message());

        verify(adminReportService).deletePostByAdmin(1L);
    }

    @Test
    @DisplayName("Should delete opportunity by admin successfully")
    void deleteOpportunityByAdminSuccess() {
        OpportunityDTO opportunityDTO = new OpportunityDTO(
                1L,
                "Opportunity Title",
                "Description",
                "Location",
                null,
                null,
                Instant.now(),
                null,
                null,
                0
        );
        ResponseDTO<OpportunityDTO> expectedResponse = new ResponseDTO<>(
                "Oportunidade deletada com sucesso",
                opportunityDTO
        );

        when(adminReportService.deleteOpportunityByAdmin(1L)).thenReturn(expectedResponse);

        ResponseDTO<OpportunityDTO> result = adminService.deleteOpportunityByAdmin(1L);

        assertNotNull(result);
        assertEquals("Oportunidade deletada com sucesso", result.message());

        verify(adminReportService).deleteOpportunityByAdmin(1L);
    }

    @Test
    @DisplayName("Should dismiss reports successfully")
    void dismissReportsSuccess() {
        ResponseDTO<String> expectedResponse = new ResponseDTO<>(
                "Denúncias descartadas com sucesso",
                "Post reports dismissed"
        );

        when(adminReportService.dismissReports(1L, "post")).thenReturn(expectedResponse);

        ResponseDTO<String> result = adminService.dismissReports(1L, "post");

        assertNotNull(result);
        assertEquals("Denúncias descartadas com sucesso", result.message());

        verify(adminReportService).dismissReports(1L, "post");
    }

    // USER MODERATION TESTS

    @Test
    @DisplayName("Should ban user successfully")
    void banUserSuccess() {
        BanUserRequestDTO banRequest = new BanUserRequestDTO(1L, 2L, "Violated terms of service", null);
        ResponseDTO<String> expectedResponse = new ResponseDTO<>(
                "Usuário banido com sucesso",
                "User banned"
        );

        when(adminModerationService.banUser(banRequest)).thenReturn(expectedResponse);

        ResponseDTO<String> result = adminService.banUser(banRequest);

        assertNotNull(result);
        assertEquals("Usuário banido com sucesso", result.message());

        verify(adminModerationService).banUser(banRequest);
    }

    @Test
    @DisplayName("Should suspend user successfully")
    void suspendUserSuccess() {
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(
                1L,
                2L,
                7,
                "Inappropriate behavior",
                null
        );
        ResponseDTO<String> expectedResponse = new ResponseDTO<>(
                "Usuário suspenso com sucesso",
                "User suspended for 7 days"
        );

        when(adminModerationService.suspendUser(suspendRequest)).thenReturn(expectedResponse);

        ResponseDTO<String> result = adminService.suspendUser(suspendRequest);

        assertNotNull(result);
        assertEquals("Usuário suspenso com sucesso", result.message());

        verify(adminModerationService).suspendUser(suspendRequest);
    }
}

