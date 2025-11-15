package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportRequestDTO;
import com.example.weuniteauth.service.ReportService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private ReportDTO sampleReport() {
        UserDTO reporter = new UserDTO("1", "Reporter", "reporter", "BASIC", null, "report@test.com", null, null, false, Instant.now(), Instant.now());
        return new ReportDTO("10", reporter, "POST", 99L, "Spam", "PENDING", Instant.now());
    }

    @Test
    void createReportShouldReturnCreatedStatus() throws Exception {
        ResponseDTO<ReportDTO> response = new ResponseDTO<>("created", sampleReport());
        when(reportService.createReport(anyLong(), any(ReportRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/reports/create/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"POST","entityId":99,"reason":"Spam"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("POST"));
    }

    @Test
    void getPendingReportsShouldReturnList() throws Exception {
        when(reportService.getAllPendingReports()).thenReturn(List.of(sampleReport()));

        mockMvc.perform(get("/api/reports/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reason").value("Spam"));
    }

    @Test
    void getReportCountShouldReturnNumber() throws Exception {
        when(reportService.getReportCount(99L, "POST")).thenReturn(3L);

        mockMvc.perform(get("/api/reports/count/{entityId}/{type}", 99L, "POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }
}

