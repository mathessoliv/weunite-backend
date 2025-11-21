package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.BanUserRequestDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.SuspendUserRequestDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.dto.report.ReportSummaryDTO;
import com.example.weuniteauth.dto.report.ReportedOpportunityDetailDTO;
import com.example.weuniteauth.dto.report.ReportedPostDetailDTO;
import com.example.weuniteauth.service.admin.AdminModerationService;
import com.example.weuniteauth.service.admin.AdminReportService;
import com.example.weuniteauth.service.admin.AdminStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminStatsService adminStatsService;

    @Mock
    private AdminReportService adminReportService;

    @Mock
    private AdminModerationService adminModerationService;

    @InjectMocks
    private AdminService adminService;

    private AdminStatsDTO statsDTO;
    private ResponseDTO<String> messageResponse;

    @BeforeEach
    void setUp() {
        statsDTO = new AdminStatsDTO(10L, 5L, 3L, 1.5, null);
        messageResponse = new ResponseDTO<>("ok", "body");
    }

    @Test
    void shouldDelegateToStatsService() {
        List<MonthlyDataDTO> monthlyData = List.of(new MonthlyDataDTO("JAN", 1L, 2L));
        List<UserTypeDataDTO> userTypeData = List.of(new UserTypeDataDTO("ATHLETE", 10L));

        when(adminStatsService.getAdminStats()).thenReturn(statsDTO);
        when(adminStatsService.getMonthlyData()).thenReturn(monthlyData);
        when(adminStatsService.getUserTypeData()).thenReturn(userTypeData);

        assertThat(adminService.getAdminStats()).isEqualTo(statsDTO);
        assertThat(adminService.getMonthlyData()).isEqualTo(monthlyData);
        assertThat(adminService.getUserTypeData()).isEqualTo(userTypeData);
    }

    @Test
    void shouldDelegateToReportService() {
        List<ReportSummaryDTO> summaries = List.of(new ReportSummaryDTO(1L, "POST", 3L));
        ReportedPostDetailDTO postDetail = new ReportedPostDetailDTO(null, List.of(), 0L, "OPEN");
        ReportedOpportunityDetailDTO opportunityDetail = new ReportedOpportunityDetailDTO(
                new OpportunityDTO(1L, "title", "desc", "remote", null, Set.of(), Instant.now(), Instant.now(), null, 0),
                List.of(), 0L, "OPEN"
        );
        ResponseDTO<PostDTO> deletePostResponse = new ResponseDTO<>("deleted", null);
        ResponseDTO<OpportunityDTO> deleteOpportunityResponse = new ResponseDTO<>("deleted", null);

        when(adminReportService.getPostsWithManyReports()).thenReturn(summaries);
        when(adminReportService.getReportedPostsDetails()).thenReturn(List.of(postDetail));
        when(adminReportService.getReportedPostDetail(anyLong())).thenReturn(postDetail);
        when(adminReportService.getOpportunitiesWithManyReports()).thenReturn(summaries);
        when(adminReportService.getReportedOpportunitiesDetails()).thenReturn(List.of(opportunityDetail));
        when(adminReportService.getReportedOpportunityDetail(anyLong())).thenReturn(opportunityDetail);
        when(adminReportService.deletePostByAdmin(anyLong())).thenReturn(deletePostResponse);
        when(adminReportService.deleteOpportunityByAdmin(anyLong())).thenReturn(deleteOpportunityResponse);
        when(adminReportService.dismissReports(anyLong(), any())).thenReturn(messageResponse);
        when(adminReportService.markReportAsReviewed(anyLong(), any())).thenReturn(messageResponse);
        when(adminReportService.resolveReports(anyLong(), any())).thenReturn(messageResponse);

        assertThat(adminService.getPostsWithManyReports()).isEqualTo(summaries);
        assertThat(adminService.getReportedPostsDetails()).containsExactly(postDetail);
        assertThat(adminService.getReportedPostDetail(1L)).isEqualTo(postDetail);
        assertThat(adminService.getOpportunitiesWithManyReports()).isEqualTo(summaries);
        assertThat(adminService.getReportedOpportunitiesDetails()).containsExactly(opportunityDetail);
        assertThat(adminService.getReportedOpportunityDetail(2L)).isEqualTo(opportunityDetail);
        assertThat(adminService.deletePostByAdmin(1L)).isEqualTo(deletePostResponse);
        assertThat(adminService.deleteOpportunityByAdmin(1L)).isEqualTo(deleteOpportunityResponse);
        assertThat(adminService.dismissReports(1L, "POST")).isEqualTo(messageResponse);
        assertThat(adminService.markReportAsReviewed(1L, "POST")).isEqualTo(messageResponse);
        assertThat(adminService.resolveReports(1L, "POST")).isEqualTo(messageResponse);
    }

    @Test
    void shouldDelegateToModerationService() {
        when(adminModerationService.banUser(any(BanUserRequestDTO.class))).thenReturn(messageResponse);
        when(adminModerationService.suspendUser(any(SuspendUserRequestDTO.class))).thenReturn(messageResponse);

        assertThat(adminService.banUser(new BanUserRequestDTO(1L, 2L, "reason long", null))).isEqualTo(messageResponse);
        assertThat(adminService.suspendUser(new SuspendUserRequestDTO(1L, 2L, 5, "reason long", null))).isEqualTo(messageResponse);
    }
}

