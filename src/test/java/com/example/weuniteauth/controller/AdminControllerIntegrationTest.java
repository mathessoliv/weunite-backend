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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    void getStatsShouldReturnDto() throws Exception {
        AdminStatsDTO stats = new AdminStatsDTO(
                10L,
                5L,
                20L,
                1.5,
                new PreviousMonthStatsDTO(8L, 3L, 15L, 1.2)
        );
        when(adminService.getAdminStats()).thenReturn(stats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPosts").value(10));
    }

    @Test
    void getReportedPostsShouldReturnList() throws Exception {
        ReportSummaryDTO summary = new ReportSummaryDTO(1L, "POST", 4L);
        when(adminService.getPostsWithManyReports()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/admin/posts/reported"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityId").value(1L));
    }

    @Test
    void deletePostShouldReturnResponseMessage() throws Exception {
        ResponseDTO<PostDTO> response = new ResponseDTO<>(
                "Post removed",
                new PostDTO("1", "text", null, null, List.of(), List.of(), Instant.now(), Instant.now(), sampleUser())
        );
        when(adminService.deletePostByAdmin(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/admin/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post removed"));
    }

    @Test
    void resolveReportsShouldReturnMessage() throws Exception {
        ResponseDTO<String> response = new ResponseDTO<>("Resolved", "OK");
        when(adminService.resolveReports(2L, "POST")).thenReturn(response);

        mockMvc.perform(put("/api/admin/reports/resolve/{entityId}/{type}", 2L, "POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resolved"));
    }

    @Test
    void banUserShouldCallService() throws Exception {
        BanUserRequestDTO request = new BanUserRequestDTO(10L, 1L, "Reason extended", null);
        ResponseDTO<String> response = new ResponseDTO<>("Banned", "OK");
        when(adminService.banUser(request)).thenReturn(response);

        mockMvc.perform(post("/api/admin/users/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":10,"adminId":1,"reason":"Reason extended","reportId":null}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Banned"));
    }

    @Test
    void getReportedOpportunityDetailShouldReturnPayload() throws Exception {
        ReportDTO report = new ReportDTO("1", sampleUser(), "POST", 3L, "Spam", "PENDING", Instant.now());
        ReportedOpportunityDetailDTO detail = new ReportedOpportunityDetailDTO(
                new OpportunityDTO(1L, "Opportunity", "desc", "Remote", null, Set.of(), Instant.now(), Instant.now(), sampleUser()),
                List.of(report),
                1L,
                "OPEN"
        );
        when(adminService.getReportedOpportunityDetail(5L)).thenReturn(detail);

        mockMvc.perform(get("/api/admin/opportunities/reported/{opportunityId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReports").value(1));
    }

    @Test
    void getMonthlyDataShouldReturnList() throws Exception {
        when(adminService.getMonthlyData()).thenReturn(List.of(new MonthlyDataDTO("JAN", 5L, 3L)));

        mockMvc.perform(get("/api/admin/stats/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("JAN"));
    }

    @Test
    void suspendUserShouldReturnMessage() throws Exception {
        SuspendUserRequestDTO request = new SuspendUserRequestDTO(9L, 1L, 5, "Suspended for review", null);
        ResponseDTO<String> response = new ResponseDTO<>("Suspended", "OK");
        when(adminService.suspendUser(request)).thenReturn(response);

        mockMvc.perform(post("/api/admin/users/suspend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":9,"adminId":1,"durationInDays":5,"reason":"Suspended for review","reportId":null}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Suspended"));
    }

    private UserDTO sampleUser() {
        return new UserDTO("1", "Test", "test_user", "BASIC", null, "test@example.com", null, null, false, Instant.now(), Instant.now());
    }
}
