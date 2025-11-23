package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.BanUserRequestDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.SuspendUserRequestDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.dto.admin.OpportunityCategoryWithSkillsDTO;
import com.example.weuniteauth.dto.report.ReportSummaryDTO;
import com.example.weuniteauth.dto.report.ReportedPostDetailDTO;
import com.example.weuniteauth.dto.report.ReportedOpportunityDetailDTO;
import com.example.weuniteauth.service.admin.AdminReportService;
import com.example.weuniteauth.service.admin.AdminModerationService;
import com.example.weuniteauth.service.admin.AdminStatsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço facade para operações de administração.
 * Delega para serviços especializados:
 * - AdminStatsService: Estatísticas do dashboard
 * - AdminReportService: Gerenciamento de denúncias
 * - AdminModerationService: Moderação de usuários
 */
@Service
public class AdminService {

    private final AdminStatsService adminStatsService;
    private final AdminReportService adminReportService;
    private final AdminModerationService adminModerationService;

    public AdminService(AdminStatsService adminStatsService,
                        AdminReportService adminReportService,
                        AdminModerationService adminModerationService) {
        this.adminStatsService = adminStatsService;
        this.adminReportService = adminReportService;
        this.adminModerationService = adminModerationService;
    }

    // ========== Delegação para AdminStatsService ==========

    public AdminStatsDTO getAdminStats() {
        return adminStatsService.getAdminStats();
    }

    public List<MonthlyDataDTO> getMonthlyData() {
        return adminStatsService.getMonthlyData();
    }

    public List<UserTypeDataDTO> getUserTypeData() {
        return adminStatsService.getUserTypeData();
    }

    public List<OpportunityCategoryWithSkillsDTO> getOpportunitiesWithSkills() {
        return adminStatsService.getOpportunitiesWithSkills();
    }

    // ========== Delegação para AdminReportService ==========

    public List<ReportSummaryDTO> getPostsWithManyReports() {
        return adminReportService.getPostsWithManyReports();
    }

    public List<ReportedPostDetailDTO> getReportedPostsDetails() {
        return adminReportService.getReportedPostsDetails();
    }

    public ReportedPostDetailDTO getReportedPostDetail(Long postId) {
        return adminReportService.getReportedPostDetail(postId);
    }

    public ResponseDTO<PostDTO> deletePostByAdmin(Long postId) {
        return adminReportService.deletePostByAdmin(postId);
    }

    public ResponseDTO<PostDTO> restorePostByAdmin(Long postId) {
        return adminReportService.restorePostByAdmin(postId);
    }

    public List<ReportSummaryDTO> getOpportunitiesWithManyReports() {
        return adminReportService.getOpportunitiesWithManyReports();
    }

    public List<ReportedOpportunityDetailDTO> getReportedOpportunitiesDetails() {
        return adminReportService.getReportedOpportunitiesDetails();
    }

    public ReportedOpportunityDetailDTO getReportedOpportunityDetail(Long opportunityId) {
        return adminReportService.getReportedOpportunityDetail(opportunityId);
    }

    public ResponseDTO<OpportunityDTO> deleteOpportunityByAdmin(Long opportunityId) {
        return adminReportService.deleteOpportunityByAdmin(opportunityId);
    }

    public ResponseDTO<OpportunityDTO> restoreOpportunityByAdmin(Long opportunityId) {
        return adminReportService.restoreOpportunityByAdmin(opportunityId);
    }

    public ResponseDTO<String> dismissReports(Long entityId, String type) {
        return adminReportService.dismissReports(entityId, type);
    }

    public ResponseDTO<String> markReportAsReviewed(Long entityId, String type) {
        return adminReportService.markReportAsReviewed(entityId, type);
    }

    public ResponseDTO<String> resolveReports(Long entityId, String type) {
        return adminReportService.resolveReports(entityId, type);
    }

    // ========== Delegação para AdminModerationService ==========

    public ResponseDTO<String> banUser(BanUserRequestDTO request) {
        return adminModerationService.banUser(request);
    }

    public ResponseDTO<String> suspendUser(SuspendUserRequestDTO request) {
        return adminModerationService.suspendUser(request);
    }
}

