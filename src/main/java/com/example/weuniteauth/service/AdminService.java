package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.PreviousMonthStatsDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportSummaryDTO;
import com.example.weuniteauth.dto.report.ReportedPostDetailDTO;
import com.example.weuniteauth.dto.report.ReportedOpportunityDetailDTO;
import com.example.weuniteauth.exceptions.opportunity.OpportunityNotFoundException;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.mapper.OpportunityMapper;
import com.example.weuniteauth.mapper.PostMapper;
import com.example.weuniteauth.mapper.ReportMapper;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.ReportRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final OpportunityMapper opportunityMapper;
    private final ReportMapper reportMapper;
    private static final Long REPORT_THRESHOLD = 1L;

    public AdminService(ReportRepository reportRepository,
                        PostRepository postRepository,
                        OpportunityRepository opportunityRepository,
                        UserRepository userRepository,
                        PostMapper postMapper,
                        OpportunityMapper opportunityMapper,
                        ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.opportunityMapper = opportunityMapper;
        this.reportMapper = reportMapper;
    }

    @Transactional(readOnly = true)
    public List<ReportSummaryDTO> getPostsWithManyReports() {
        List<Object[]> results = reportRepository.findEntitiesWithManyReports(
                Report.ReportType.POST,
                REPORT_THRESHOLD
        );

        return results.stream()
                .map(result -> new ReportSummaryDTO(
                        (Long) result[0],
                        ((Report.ReportType) result[1]).name(),
                        (Long) result[2]
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportSummaryDTO> getOpportunitiesWithManyReports() {
        List<Object[]> results = reportRepository.findEntitiesWithManyReports(
                Report.ReportType.OPPORTUNITY,
                REPORT_THRESHOLD
        );

        return results.stream()
                .map(result -> new ReportSummaryDTO(
                        (Long) result[0],
                        ((Report.ReportType) result[1]).name(),
                        (Long) result[2]
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseDTO<PostDTO> deletePostByAdmin(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        markReportsAsReviewed(postId, Report.ReportType.POST);

        postRepository.delete(post);

        return postMapper.toResponseDTO("Post excluído com sucesso pelo administrador", post);
    }

    @Transactional
    public ResponseDTO<OpportunityDTO> deleteOpportunityByAdmin(Long opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        markReportsAsReviewed(opportunityId, Report.ReportType.OPPORTUNITY);

        opportunityRepository.delete(opportunity);

        return opportunityMapper.toResponseDTO("Oportunidade excluída com sucesso pelo administrador", opportunity);
    }

    @Transactional
    public ResponseDTO<String> dismissReports(Long entityId, String type) {
        Report.ReportType reportType = Report.ReportType.valueOf(type.toUpperCase());
        List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                entityId,
                reportType,
                Report.ReportStatus.PENDING
        );

        reports.forEach(report -> report.setStatus(Report.ReportStatus.DISMISSED));
        reportRepository.saveAll(reports);

        return new ResponseDTO<>(
                "Denúncias descartadas com sucesso",
                reports.size() + " denúncias foram descartadas"
        );
    }

    @Transactional
    public ResponseDTO<String> markReportAsReviewed(Long entityId, String type) {
        Report.ReportType reportType = Report.ReportType.valueOf(type.toUpperCase());
        List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                entityId,
                reportType,
                Report.ReportStatus.PENDING
        );

        reports.forEach(report -> report.setStatus(Report.ReportStatus.REVIEWED));
        reportRepository.saveAll(reports);

        return new ResponseDTO<>(
                "Denúncias marcadas como revisadas",
                reports.size() + " denúncias foram marcadas como em análise"
        );
    }

    @Transactional
    public ResponseDTO<String> resolveReports(Long entityId, String type) {
        Report.ReportType reportType = Report.ReportType.valueOf(type.toUpperCase());
        List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                entityId,
                reportType,
                Report.ReportStatus.PENDING
        );

        List<Report> reviewedReports = reportRepository.findByEntityIdAndTypeAndStatus(
                entityId,
                reportType,
                Report.ReportStatus.REVIEWED
        );

        reports.forEach(report -> report.setStatus(Report.ReportStatus.DISMISSED));
        reviewedReports.forEach(report -> report.setStatus(Report.ReportStatus.DISMISSED));
        
        reportRepository.saveAll(reports);
        reportRepository.saveAll(reviewedReports);

        int totalResolved = reports.size() + reviewedReports.size();

        return new ResponseDTO<>(
                "Denúncias resolvidas com sucesso",
                totalResolved + " denúncias foram resolvidas e o conteúdo foi mantido"
        );
    }

    private void markReportsAsReviewed(Long entityId, Report.ReportType type) {
        List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                entityId,
                type,
                Report.ReportStatus.PENDING
        );
        reports.forEach(report -> report.setStatus(Report.ReportStatus.REVIEWED));
        reportRepository.saveAll(reports);
    }

    @Transactional(readOnly = true)
    public List<ReportedPostDetailDTO> getReportedPostsDetails() {
        List<Object[]> results = reportRepository.findEntitiesWithManyReports(
                Report.ReportType.POST,
                REPORT_THRESHOLD
        );

        return results.stream()
                .map(result -> {
                    Long postId = (Long) result[0];
                    Long reportCount = (Long) result[2];

                    Post post = postRepository.findById(postId)
                            .orElseThrow(PostNotFoundException::new);

                    List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                            postId,
                            Report.ReportType.POST,
                            Report.ReportStatus.PENDING
                    );

                    PostDTO postDTO = postMapper.toPostDTO(post);
                    List<ReportDTO> reportDTOs = reportMapper.toReportDTOList(reports);

                    return new ReportedPostDetailDTO(
                            postDTO,
                            reportDTOs,
                            reportCount,
                            "pending"
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportedOpportunityDetailDTO> getReportedOpportunitiesDetails() {
        List<Object[]> results = reportRepository.findEntitiesWithManyReports(
                Report.ReportType.OPPORTUNITY,
                REPORT_THRESHOLD
        );

        return results.stream()
                .map(result -> {
                    Long opportunityId = (Long) result[0];
                    Long reportCount = (Long) result[2];

                    Opportunity opportunity = opportunityRepository.findById(opportunityId)
                            .orElseThrow(OpportunityNotFoundException::new);

                    List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                            opportunityId,
                            Report.ReportType.OPPORTUNITY,
                            Report.ReportStatus.PENDING
                    );

                    OpportunityDTO opportunityDTO = opportunityMapper.toOpportunityDTO(opportunity);
                    List<ReportDTO> reportDTOs = reportMapper.toReportDTOList(reports);

                    return new ReportedOpportunityDetailDTO(
                            opportunityDTO,
                            reportDTOs,
                            reportCount,
                            "pending"
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportedPostDetailDTO getReportedPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                postId,
                Report.ReportType.POST,
                Report.ReportStatus.PENDING
        );

        PostDTO postDTO = postMapper.toPostDTO(post);
        List<ReportDTO> reportDTOs = reportMapper.toReportDTOList(reports);

        return new ReportedPostDetailDTO(
                postDTO,
                reportDTOs,
                (long) reports.size(),
                reports.isEmpty() ? "resolved" : "pending"
        );
    }

    @Transactional(readOnly = true)
    public ReportedOpportunityDetailDTO getReportedOpportunityDetail(Long opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        List<Report> reports = reportRepository.findByEntityIdAndTypeAndStatus(
                opportunityId,
                Report.ReportType.OPPORTUNITY,
                Report.ReportStatus.PENDING
        );

        OpportunityDTO opportunityDTO = opportunityMapper.toOpportunityDTO(opportunity);
        List<ReportDTO> reportDTOs = reportMapper.toReportDTOList(reports);

        return new ReportedOpportunityDetailDTO(
                opportunityDTO,
                reportDTOs,
                (long) reports.size(),
                reports.isEmpty() ? "resolved" : "pending"
        );
    }

    /**
     * Calcula estatísticas gerais do dashboard
     */
    @Transactional(readOnly = true)
    public AdminStatsDTO getAdminStats() {
        Instant now = Instant.now();
        Instant tenDaysAgo = now.minus(10, ChronoUnit.DAYS);
        
        // Estatísticas do mês atual
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate startOfPreviousMonth = startOfMonth.minusMonths(1);
        LocalDate endOfPreviousMonth = startOfMonth.minusDays(1);
        
        Instant startOfLastMonth = startOfPreviousMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfLastMonth = endOfPreviousMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        // Totais atuais
        Long totalPosts = postRepository.count();
        Long totalOpportunities = opportunityRepository.count();
        Long activeUsers = userRepository.countActiveUsersByPostActivity(tenDaysAgo);
        
        // Calcular taxa de engajamento
        Long totalLikes = postRepository.countTotalLikes();
        Long totalComments = postRepository.countTotalComments();
        Double engagementRate = calculateEngagementRate(totalPosts, totalLikes, totalComments);
        
        // Estatísticas do mês anterior
        Long previousMonthPosts = postRepository.countPostsBetweenDates(startOfLastMonth, endOfLastMonth);
        Long previousMonthOpportunities = opportunityRepository.countOpportunitiesBetweenDates(startOfLastMonth, endOfLastMonth);
        
        // Para o mês anterior, vamos usar uma aproximação baseada na tendência
        Instant thirtyDaysAgo = now.minus(30, ChronoUnit.DAYS);
        Long previousActiveUsers = userRepository.countActiveUsersByPostActivity(thirtyDaysAgo);
        
        // Engajamento do mês anterior (simplificado)
        Double previousEngagementRate = engagementRate * 0.95; // Aproximação: 95% do atual
        
        PreviousMonthStatsDTO previousMonth = new PreviousMonthStatsDTO(
                previousMonthPosts,
                previousMonthOpportunities,
                previousActiveUsers,
                previousEngagementRate
        );
        
        return new AdminStatsDTO(
                totalPosts,
                totalOpportunities,
                activeUsers,
                engagementRate,
                previousMonth
        );
    }
    
    /**
     * Calcula a taxa de engajamento
     * Fórmula: (Total de Likes + Total de Comentários) / Total de Posts * 100
     */
    private Double calculateEngagementRate(Long totalPosts, Long totalLikes, Long totalComments) {
        if (totalPosts == 0) {
            return 0.0;
        }
        
        Long totalEngagement = totalLikes + totalComments;
        return (totalEngagement.doubleValue() / totalPosts.doubleValue()) * 100;
    }
    
    /**
     * Retorna dados mensais dos últimos 6 meses
     */
    @Transactional(readOnly = true)
    public List<MonthlyDataDTO> getMonthlyData() {
        LocalDate today = LocalDate.now();
        List<MonthlyDataDTO> monthlyData = new ArrayList<>();
        
        // Últimos 6 meses
        for (int i = 5; i >= 0; i--) {
            LocalDate targetMonth = today.minusMonths(i);
            LocalDate startOfMonth = targetMonth.withDayOfMonth(1);
            LocalDate endOfMonth = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth());
            
            Instant startInstant = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant endInstant = endOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
            
            Long postsCount = postRepository.countPostsBetweenDates(startInstant, endInstant);
            Long opportunitiesCount = opportunityRepository.countOpportunitiesBetweenDates(startInstant, endInstant);
            
            // Nome do mês abreviado em português
            String monthName = targetMonth.getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
                    .substring(0, 3);
            
            monthlyData.add(new MonthlyDataDTO(
                    monthName.substring(0, 1).toUpperCase() + monthName.substring(1),
                    postsCount,
                    opportunitiesCount
            ));
        }
        
        return monthlyData;
    }
    
    /*
     * Retorna distribuição de usuários por tipo
     */
    @Transactional(readOnly = true)
    public List<UserTypeDataDTO> getUserTypeData() {
        Long athletesCount = userRepository.countAthletes();
        Long companiesCount = userRepository.countCompanies();
        
        List<UserTypeDataDTO> userTypeData = new ArrayList<>();
        userTypeData.add(new UserTypeDataDTO("Atletas", athletesCount));
        userTypeData.add(new UserTypeDataDTO("Empresas", companiesCount));
        
        return userTypeData;
    }
}

