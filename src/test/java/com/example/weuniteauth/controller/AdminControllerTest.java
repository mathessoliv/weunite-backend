package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.BanUserRequestDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.PreviousMonthStatsDTO;
import com.example.weuniteauth.dto.admin.SuspendUserRequestDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportSummaryDTO;
import com.example.weuniteauth.dto.report.ReportedOpportunityDetailDTO;
import com.example.weuniteauth.dto.report.ReportedPostDetailDTO;
import com.example.weuniteauth.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private UserDTO sampleUser;
    private AdminStatsDTO statsDTO;

    @BeforeEach
    void setUp() {
        sampleUser = new UserDTO("1", "Admin", "admin", "ADMIN", null, "admin@test.com",
                null, null, false, Instant.now(), Instant.now(), null, null, null, null, null, List.of());
        statsDTO = new AdminStatsDTO(
                10L,
                5L,
                20L,
                2.5,
                new PreviousMonthStatsDTO(8L, 3L, 15L, 1.5)
        );
    }

    @Test
    void shouldReturnStats() {
        when(adminService.getAdminStats()).thenReturn(statsDTO);

        ResponseEntity<AdminStatsDTO> response = adminController.getAdminStats();

        assertThat(response.getBody()).isEqualTo(statsDTO);
    }

    @Test
    void shouldReturnMonthlyData() {
        List<MonthlyDataDTO> monthlyData = List.of(new MonthlyDataDTO("JAN", 1L, 2L));
        when(adminService.getMonthlyData()).thenReturn(monthlyData);

        assertThat(adminController.getMonthlyData().getBody()).containsExactlyElementsOf(monthlyData);
    }

    @Test
    void shouldReturnUserTypeData() {
        List<UserTypeDataDTO> userTypeData = List.of(new UserTypeDataDTO("ATHLETE", 10L));
        when(adminService.getUserTypeData()).thenReturn(userTypeData);

        assertThat(adminController.getUserTypeData().getBody()).isEqualTo(userTypeData);
    }

    @Test
    void shouldHandleReportedPosts() {
        ReportSummaryDTO summary = new ReportSummaryDTO(1L, "POST", 3L);
        ReportedPostDetailDTO detail = new ReportedPostDetailDTO(
                new PostDTO("1", "text", null, null, List.of(), List.of(), Instant.now(), Instant.now(), sampleUser),
                List.of(new ReportDTO("1", sampleUser, "POST", 1L, "reason", "OPEN", Instant.now(), null, null)),
                1L,
                "OPEN"
        );
        when(adminService.getPostsWithManyReports()).thenReturn(List.of(summary));
        when(adminService.getReportedPostsDetails()).thenReturn(List.of(detail));

        assertThat(adminController.getReportedPosts().getBody()).containsExactly(summary);
        assertThat(adminController.getReportedPostsDetails().getBody()).containsExactly(detail);
    }

    @Test
    void shouldHandleReportedPostDetail() {
        ReportedPostDetailDTO detail = new ReportedPostDetailDTO(
                new PostDTO("1", "text", null, null, List.of(), List.of(), Instant.now(), Instant.now(), sampleUser),
                List.of(new ReportDTO("1", sampleUser, "POST", 1L, "reason", "OPEN", Instant.now(), null, null)),
                1L,
                "OPEN"
        );
        when(adminService.getReportedPostDetail(2L)).thenReturn(detail);

        assertThat(adminController.getReportedPostDetail(2L).getBody()).isEqualTo(detail);
    }

    @Test
    void shouldHandleReportedOpportunities() {
        ReportSummaryDTO summary = new ReportSummaryDTO(1L, "OPPORTUNITY", 2L);
        ReportedOpportunityDetailDTO detail = new ReportedOpportunityDetailDTO(
                new OpportunityDTO(1L, "title", "desc", "Remote", null, Set.of(), Instant.now(), Instant.now(), sampleUser, 0),
                List.of(),
                0L,
                "OPEN"
        );
        when(adminService.getOpportunitiesWithManyReports()).thenReturn(List.of(summary));
        when(adminService.getReportedOpportunitiesDetails()).thenReturn(List.of(detail));

        assertThat(adminController.getReportedOpportunities().getBody()).containsExactly(summary);
        assertThat(adminController.getReportedOpportunitiesDetails().getBody()).containsExactly(detail);
    }

    @Test
    void shouldHandleReportedOpportunityDetail() {
        ReportedOpportunityDetailDTO detail = new ReportedOpportunityDetailDTO(
                new OpportunityDTO(1L, "title", "desc", "Remote", null, Set.of(), Instant.now(), Instant.now(), sampleUser, 0),
                List.of(),
                0L,
                "OPEN"
        );
        when(adminService.getReportedOpportunityDetail(10L)).thenReturn(detail);

        assertThat(adminController.getReportedOpportunityDetail(10L).getBody()).isEqualTo(detail);
    }

    @Test
    void shouldDeletePostAndOpportunity() {
        ResponseDTO<PostDTO> postResponse = new ResponseDTO<>("deleted", null);
        when(adminService.deletePostByAdmin(3L)).thenReturn(postResponse);
        assertThat(adminController.deletePost(3L).getBody()).isEqualTo(postResponse);

        ResponseDTO<OpportunityDTO> opportunityResponse = new ResponseDTO<>("deleted", null);
        when(adminService.deleteOpportunityByAdmin(4L)).thenReturn(opportunityResponse);
        assertThat(adminController.deleteOpportunity(4L).getBody()).isEqualTo(opportunityResponse);
    }

    @Test
    void shouldHandleReportActions() {
        ResponseDTO<String> dto = new ResponseDTO<>("ok", "body");
        when(adminService.dismissReports(1L, "POST")).thenReturn(dto);
        when(adminService.markReportAsReviewed(1L, "POST")).thenReturn(dto);
        when(adminService.resolveReports(1L, "POST")).thenReturn(dto);

        assertThat(adminController.dismissReports(1L, "POST").getBody()).isEqualTo(dto);
        assertThat(adminController.markReportAsReviewed(1L, "POST").getBody()).isEqualTo(dto);
        assertThat(adminController.resolveReports(1L, "POST").getBody()).isEqualTo(dto);
    }

    @Test
    void shouldBanAndSuspendUser() {
        ResponseDTO<String> dto = new ResponseDTO<>("ok", "body");
        BanUserRequestDTO banRequest = new BanUserRequestDTO(1L, 2L, "Reason long enough", null);
        SuspendUserRequestDTO suspendRequest = new SuspendUserRequestDTO(1L, 2L, 5, "Another long reason", null);

        when(adminService.banUser(banRequest)).thenReturn(dto);
        when(adminService.suspendUser(suspendRequest)).thenReturn(dto);

        assertThat(adminController.banUser(banRequest).getBody()).isEqualTo(dto);
        assertThat(adminController.suspendUser(suspendRequest).getBody()).isEqualTo(dto);
    }
}
