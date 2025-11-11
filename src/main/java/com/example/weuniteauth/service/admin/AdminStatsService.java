package com.example.weuniteauth.service.admin;

import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.PreviousMonthStatsDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Serviço responsável por calcular estatísticas do dashboard admin.
 * Lida com métricas gerais, dados mensais e distribuição de usuários.
 */
@Service
public class AdminStatsService {

    private final PostRepository postRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public AdminStatsService(PostRepository postRepository, 
                             OpportunityRepository opportunityRepository, 
                             UserRepository userRepository) {
        this.postRepository = postRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
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
     * Calcula a taxa de engajamento.
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
    
    /**
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

