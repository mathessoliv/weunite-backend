package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ReportMapper Tests")
class ReportMapperTest {

    @Autowired
    private ReportMapper reportMapper;

    private Report testReport;
    private User testReporter;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");

        testReporter = new Athlete();
        testReporter.setId(1L);
        testReporter.setUsername("reporter");
        testReporter.setEmail("reporter@test.com");
        testReporter.setName("Reporter User");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testReporter.setRole(roles);
        testReporter.setCreatedAt(Instant.now());

        testReport = new Report();
        testReport.setId(1L);
        testReport.setReporter(testReporter);
        testReport.setType(Report.ReportType.POST);
        testReport.setEntityId(100L);
        testReport.setReason("Conteúdo inadequado");
        testReport.setStatus(Report.ReportStatus.PENDING);
        testReport.setCreatedAt(Instant.now());
    }

    // TO REPORT DTO TESTS

    @Test
    @DisplayName("Should convert Report entity to ReportDTO")
    void toReportDTO() {
        ReportDTO result = reportMapper.toReportDTO(testReport);

        assertNotNull(result);
        assertEquals("1", result.id());
        assertNotNull(result.reporter());
        assertEquals("reporter", result.reporter().username());
        assertEquals("POST", result.type());
        assertEquals(100L, result.entityId());
        assertEquals("Conteúdo inadequado", result.reason());
        assertEquals("PENDING", result.status());
        assertNotNull(result.createdAt());
    }

    @Test
    @DisplayName("Should convert report with different types")
    void toReportDTODifferentTypes() {
        testReport.setType(Report.ReportType.POST);
        ReportDTO resultPost = reportMapper.toReportDTO(testReport);
        assertEquals("POST", resultPost.type());

        testReport.setType(Report.ReportType.OPPORTUNITY);
        ReportDTO resultOpportunity = reportMapper.toReportDTO(testReport);
        assertEquals("OPPORTUNITY", resultOpportunity.type());
    }

    @Test
    @DisplayName("Should convert report with different statuses")
    void toReportDTODifferentStatuses() {
        testReport.setStatus(Report.ReportStatus.PENDING);
        ReportDTO resultPending = reportMapper.toReportDTO(testReport);
        assertEquals("PENDING", resultPending.status());

        testReport.setStatus(Report.ReportStatus.RESOLVED);
        ReportDTO resultResolved = reportMapper.toReportDTO(testReport);
        assertEquals("RESOLVED", resultResolved.status());

        testReport.setStatus(Report.ReportStatus.DISMISSED);
        ReportDTO resultDismissed = reportMapper.toReportDTO(testReport);
        assertEquals("DISMISSED", resultDismissed.status());
    }

    @Test
    @DisplayName("Should map reporter user details correctly")
    void toReportDTOReporterDetails() {
        ReportDTO result = reportMapper.toReportDTO(testReport);

        assertNotNull(result);
        assertNotNull(result.reporter());
        assertEquals("1", result.reporter().id());
        assertEquals("Reporter User", result.reporter().name());
        assertEquals("reporter@test.com", result.reporter().email());
    }

    // TO REPORT DTO LIST TESTS

    @Test
    @DisplayName("Should convert list of reports to list of DTOs")
    void toReportDTOList() {
        Report report2 = new Report();
        report2.setId(2L);
        report2.setReporter(testReporter);
        report2.setType(Report.ReportType.OPPORTUNITY);
        report2.setEntityId(200L);
        report2.setReason("Spam");
        report2.setStatus(Report.ReportStatus.RESOLVED);
        report2.setCreatedAt(Instant.now());

        Report report3 = new Report();
        report3.setId(3L);
        report3.setReporter(testReporter);
        report3.setType(Report.ReportType.POST);
        report3.setEntityId(300L);
        report3.setReason("Comportamento ofensivo");
        report3.setStatus(Report.ReportStatus.PENDING);
        report3.setCreatedAt(Instant.now());

        List<Report> reports = new ArrayList<>();
        reports.add(testReport);
        reports.add(report2);
        reports.add(report3);

        List<ReportDTO> result = reportMapper.toReportDTOList(reports);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("POST", result.get(0).type());
        assertEquals("OPPORTUNITY", result.get(1).type());
        assertEquals("POST", result.get(2).type());
    }

    @Test
    @DisplayName("Should handle empty report list")
    void toReportDTOListEmpty() {
        List<Report> reports = new ArrayList<>();

        List<ReportDTO> result = reportMapper.toReportDTOList(reports);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null report list")
    void toReportDTOListNull() {
        List<ReportDTO> result = reportMapper.toReportDTOList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TO RESPONSE DTO TESTS

    @Test
    @DisplayName("Should create ResponseDTO with message and report")
    void toResponseDTO() {
        String message = "Denúncia criada com sucesso";

        ResponseDTO<ReportDTO> result = reportMapper.toResponseDTO(message, testReport);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("POST", result.data().type());
        assertEquals("Conteúdo inadequado", result.data().reason());
    }

    @Test
    @DisplayName("Should create ResponseDTO for resolved report")
    void toResponseDTOResolved() {
        testReport.setStatus(Report.ReportStatus.RESOLVED);
        String message = "Denúncia resolvida";

        ResponseDTO<ReportDTO> result = reportMapper.toResponseDTO(message, testReport);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertEquals("RESOLVED", result.data().status());
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should handle long reason text")
    void toReportDTOLongReason() {
        String longReason = "A".repeat(500);
        testReport.setReason(longReason);

        ReportDTO result = reportMapper.toReportDTO(testReport);

        assertNotNull(result);
        assertEquals(longReason, result.reason());
    }

    @Test
    @DisplayName("Should handle all report types correctly")
    void toReportDTOAllTypes() {
        Report.ReportType[] types = Report.ReportType.values();

        for (Report.ReportType type : types) {
            testReport.setType(type);
            ReportDTO result = reportMapper.toReportDTO(testReport);
            assertEquals(type.name(), result.type());
        }
    }
}


