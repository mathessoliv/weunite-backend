package com.example.weuniteauth.service.admin;

import com.example.weuniteauth.dto.admin.AdminStatsDTO;
import com.example.weuniteauth.dto.admin.MonthlyDataDTO;
import com.example.weuniteauth.dto.admin.UserTypeDataDTO;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminStatsService Tests")
class AdminStatsServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminStatsService adminStatsService;

    // GET ADMIN STATS TESTS

    @Test
    @DisplayName("Should calculate admin stats successfully")
    void getAdminStatsSuccess() {
        // Mock dos repositórios
        when(postRepository.count()).thenReturn(100L);
        when(opportunityRepository.count()).thenReturn(50L);
        when(userRepository.countActiveUsersByPostActivity(any(Instant.class))).thenReturn(30L);
        when(postRepository.countTotalLikes()).thenReturn(500L);
        when(postRepository.countTotalComments()).thenReturn(300L);
        when(postRepository.countPostsBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(80L);
        when(opportunityRepository.countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(40L);

        AdminStatsDTO result = adminStatsService.getAdminStats();

        assertNotNull(result);
        assertEquals(100L, result.totalPosts());
        assertEquals(50L, result.totalOpportunities());
        assertEquals(30L, result.activeUsers());
        assertNotNull(result.engagementRate());
        assertNotNull(result.previousMonth());

        verify(postRepository).count();
        verify(opportunityRepository).count();
        verify(userRepository, times(2)).countActiveUsersByPostActivity(any(Instant.class));
        verify(postRepository).countTotalLikes();
        verify(postRepository).countTotalComments();
    }

    @Test
    @DisplayName("Should calculate engagement rate correctly")
    void calculateEngagementRateCorrectly() {
        when(postRepository.count()).thenReturn(100L);
        when(opportunityRepository.count()).thenReturn(50L);
        when(userRepository.countActiveUsersByPostActivity(any(Instant.class))).thenReturn(30L);
        when(postRepository.countTotalLikes()).thenReturn(500L);
        when(postRepository.countTotalComments()).thenReturn(300L);
        when(postRepository.countPostsBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(80L);
        when(opportunityRepository.countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(40L);

        AdminStatsDTO result = adminStatsService.getAdminStats();

        // Engagement Rate = (500 + 300) / (100 * 30) * 100 = 26.67%
        assertEquals(26.67, result.engagementRate(), 0.01);
    }

    @Test
    @DisplayName("Should handle zero posts when calculating engagement rate")
    void handleZeroPostsEngagementRate() {
        when(postRepository.count()).thenReturn(0L);
        when(opportunityRepository.count()).thenReturn(50L);
        when(userRepository.countActiveUsersByPostActivity(any(Instant.class))).thenReturn(30L);
        when(postRepository.countTotalLikes()).thenReturn(0L);
        when(postRepository.countTotalComments()).thenReturn(0L);
        when(postRepository.countPostsBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(0L);
        when(opportunityRepository.countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(40L);

        AdminStatsDTO result = adminStatsService.getAdminStats();

        assertEquals(0.0, result.engagementRate());
    }

    @Test
    @DisplayName("Should handle zero active users when calculating engagement rate")
    void handleZeroActiveUsersEngagementRate() {
        when(postRepository.count()).thenReturn(100L);
        when(opportunityRepository.count()).thenReturn(50L);
        when(userRepository.countActiveUsersByPostActivity(any(Instant.class))).thenReturn(0L);
        when(postRepository.countTotalLikes()).thenReturn(200L);
        when(postRepository.countTotalComments()).thenReturn(150L);
        when(postRepository.countPostsBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(80L);
        when(opportunityRepository.countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class))).thenReturn(40L);

        AdminStatsDTO result = adminStatsService.getAdminStats();

        assertEquals(0.0, result.engagementRate());
    }

    // GET MONTHLY DATA TESTS

    @Test
    @DisplayName("Should get monthly data for last 6 months")
    void getMonthlyDataSuccess() {
        when(postRepository.countPostsBetweenDates(any(Instant.class), any(Instant.class)))
                .thenReturn(10L, 20L, 30L, 40L, 50L, 60L);
        when(opportunityRepository.countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class)))
                .thenReturn(5L, 10L, 15L, 20L, 25L, 30L);

        List<MonthlyDataDTO> result = adminStatsService.getMonthlyData();

        assertNotNull(result);
        assertEquals(6, result.size());

        // Verificar que cada mês tem dados
        result.forEach(month -> {
            assertNotNull(month.month());
            assertNotNull(month.posts());
            assertNotNull(month.opportunities());
        });

        // Verificar que countPostsBetweenDates foi chamado 6 vezes
        verify(postRepository, times(6)).countPostsBetweenDates(any(Instant.class), any(Instant.class));
        verify(opportunityRepository, times(6)).countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class));
    }

    @Test
    @DisplayName("Should return monthly data with correct month names")
    void monthlyDataHasCorrectMonthNames() {
        when(postRepository.countPostsBetweenDates(any(Instant.class), any(Instant.class)))
                .thenReturn(10L);
        when(opportunityRepository.countOpportunitiesBetweenDates(any(Instant.class), any(Instant.class)))
                .thenReturn(5L);

        List<MonthlyDataDTO> result = adminStatsService.getMonthlyData();

        // Verificar que os nomes dos meses não estão vazios
        result.forEach(month -> {
            assertNotNull(month.month());
            assertFalse(month.month().isEmpty());
            assertTrue(month.month().length() >= 3);
        });
    }

    // GET USER TYPE DATA TESTS

    @Test
    @DisplayName("Should get user type distribution successfully")
    void getUserTypeDataSuccess() {
        when(userRepository.countAthletes()).thenReturn(150L);
        when(userRepository.countCompanies()).thenReturn(50L);

        List<UserTypeDataDTO> result = adminStatsService.getUserTypeData();

        assertNotNull(result);
        assertEquals(2, result.size());

        // Verificar Atletas
        UserTypeDataDTO athletes = result.stream()
                .filter(data -> "Atletas".equals(data.name()))
                .findFirst()
                .orElse(null);
        assertNotNull(athletes);
        assertEquals(150L, athletes.value());

        // Verificar Empresas
        UserTypeDataDTO companies = result.stream()
                .filter(data -> "Empresas".equals(data.name()))
                .findFirst()
                .orElse(null);
        assertNotNull(companies);
        assertEquals(50L, companies.value());

        verify(userRepository).countAthletes();
        verify(userRepository).countCompanies();
    }

    @Test
    @DisplayName("Should handle zero users in user type data")
    void handleZeroUsersInUserTypeData() {
        when(userRepository.countAthletes()).thenReturn(0L);
        when(userRepository.countCompanies()).thenReturn(0L);

        List<UserTypeDataDTO> result = adminStatsService.getUserTypeData();

        assertNotNull(result);
        assertEquals(2, result.size());

        result.forEach(data -> {
            assertEquals(0L, data.value());
        });
    }
}

