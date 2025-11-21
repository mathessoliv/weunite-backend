package com.example.weuniteauth.service.admin;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.report.ReportSummaryDTO;
import com.example.weuniteauth.dto.report.ReportedPostDetailDTO;
import com.example.weuniteauth.exceptions.post.PostNotFoundException;
import com.example.weuniteauth.mapper.OpportunityMapper;
import com.example.weuniteauth.mapper.PostMapper;
import com.example.weuniteauth.mapper.ReportMapper;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.PostRepository;
import com.example.weuniteauth.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminReportService Tests")
class AdminReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private OpportunityMapper opportunityMapper;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private AdminReportService adminReportService;

    private Post testPost;
    private Opportunity testOpportunity;
    private Report testReport;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setText("Test post");
        testPost.setUser(testUser);
        testPost.setCreatedAt(Instant.now());

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Test opportunity");

        testReport = new Report();
        testReport.setId(1L);
        testReport.setEntityId(1L);
        testReport.setType(Report.ReportType.POST);
        testReport.setStatus(Report.ReportStatus.PENDING);
        testReport.setReason("Spam");
    }

    // GET POSTS WITH MANY REPORTS TESTS

    @Test
    @DisplayName("Should get posts with many reports successfully")
    void getPostsWithManyReportsSuccess() {
        Object[] result1 = {1L, Report.ReportType.POST, 5L};
        Object[] result2 = {2L, Report.ReportType.POST, 3L};
        List<Object[]> mockResults = Arrays.asList(result1, result2);

        when(reportRepository.findEntitiesWithManyReports(eq(Report.ReportType.POST), eq(1L)))
                .thenReturn(mockResults);

        List<ReportSummaryDTO> result = adminReportService.getPostsWithManyReports();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).entityId());
        assertEquals("POST", result.get(0).entityType());
        assertEquals(5L, result.get(0).reportCount());

        verify(reportRepository).findEntitiesWithManyReports(Report.ReportType.POST, 1L);
    }

    @Test
    @DisplayName("Should return empty list when no posts have many reports")
    void getPostsWithManyReportsEmpty() {
        when(reportRepository.findEntitiesWithManyReports(eq(Report.ReportType.POST), eq(1L)))
                .thenReturn(Arrays.asList());

        List<ReportSummaryDTO> result = adminReportService.getPostsWithManyReports();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // GET REPORTED POSTS DETAILS TESTS

    @Test
    @DisplayName("Should get reported posts details successfully")
    void getReportedPostsDetailsSuccess() {
        Object[] mockResult = new Object[]{1L, Report.ReportType.POST, 3L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(mockResult);

        PostDTO mockPostDTO = new PostDTO("1", "Test post", null, null, null, null, Instant.now(), null, null);

        when(reportRepository.findAllEntitiesWithReports(eq(Report.ReportType.POST), eq(1L)))
                .thenReturn(mockResults);
        when(postRepository.existsById(1L)).thenReturn(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(reportRepository.findByEntityIdAndType(1L, Report.ReportType.POST))
                .thenReturn(new ArrayList<>());
        when(postMapper.toPostDTO(any(Post.class))).thenReturn(mockPostDTO);
        when(reportMapper.toReportDTOList(anyList())).thenReturn(new ArrayList<>());

        List<ReportedPostDetailDTO> result = adminReportService.getReportedPostsDetails();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(reportRepository).findAllEntitiesWithReports(Report.ReportType.POST, 1L);
        verify(postRepository).existsById(1L);
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("Should filter out non-existent posts")
    void getReportedPostsDetailsFilterNonExistent() {
        Object[] mockResult = new Object[]{999L, Report.ReportType.POST, 3L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(mockResult);

        when(reportRepository.findAllEntitiesWithReports(eq(Report.ReportType.POST), eq(1L)))
                .thenReturn(mockResults);
        when(postRepository.existsById(999L)).thenReturn(false);

        List<ReportedPostDetailDTO> result = adminReportService.getReportedPostsDetails();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository).existsById(999L);
        verify(postRepository, never()).findById(anyLong());
    }

    // GET REPORTED POST DETAIL TESTS

    @Test
    @DisplayName("Should get reported post detail successfully")
    void getReportedPostDetailSuccess() {
        PostDTO mockPostDTO = new PostDTO("1", "Test post", null, null, null, null, Instant.now(), null, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(reportRepository.findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING))
                .thenReturn(Arrays.asList(testReport));
        when(postMapper.toPostDTO(any(Post.class))).thenReturn(mockPostDTO);
        when(reportMapper.toReportDTOList(anyList())).thenReturn(Arrays.asList());

        ReportedPostDetailDTO result = adminReportService.getReportedPostDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.totalReports());
        assertEquals("pending", result.status());

        verify(postRepository).findById(1L);
        verify(reportRepository).findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING);
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when post does not exist")
    void getReportedPostDetailNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () ->
                adminReportService.getReportedPostDetail(999L)
        );

        verify(postRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return resolved status when no pending reports")
    void getReportedPostDetailResolvedStatus() {
        PostDTO mockPostDTO = new PostDTO("1", "Test post", null, null, null, null, Instant.now(), null, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(reportRepository.findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING))
                .thenReturn(Arrays.asList()); // Empty list
        when(postMapper.toPostDTO(any(Post.class))).thenReturn(mockPostDTO);
        when(reportMapper.toReportDTOList(anyList())).thenReturn(Arrays.asList());

        ReportedPostDetailDTO result = adminReportService.getReportedPostDetail(1L);

        assertNotNull(result);
        assertEquals(0L, result.totalReports());
        assertEquals("resolved", result.status());
    }

    // DELETE POST BY ADMIN TESTS

    @Test
    @DisplayName("Should delete post by admin successfully")
    void deletePostByAdminSuccess() {
        PostDTO mockPostDTO = new PostDTO("1", "Test post", null, null, null, null, Instant.now(), null, null);
        ResponseDTO<PostDTO> expectedResponse = new ResponseDTO<>("Post excluído com sucesso pelo administrador", mockPostDTO);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        doNothing().when(postRepository).delete(testPost);
        when(postMapper.toResponseDTO(anyString(), any(Post.class))).thenReturn(expectedResponse);

        ResponseDTO<PostDTO> result = adminReportService.deletePostByAdmin(1L);

        assertNotNull(result);
        assertEquals("Post excluído com sucesso pelo administrador", result.message());

        verify(postRepository).findById(1L);
        verify(postRepository).delete(testPost);
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when deleting non-existent post")
    void deletePostByAdminNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () ->
                adminReportService.deletePostByAdmin(999L)
        );

        verify(postRepository).findById(999L);
        verify(postRepository, never()).delete(any(Post.class));
    }

    // DELETE OPPORTUNITY BY ADMIN TESTS

    @Test
    @DisplayName("Should delete opportunity by admin successfully")
    void deleteOpportunityByAdminSuccess() {
        OpportunityDTO mockOpportunityDTO = new OpportunityDTO(1L, "Test opportunity", null, null, null, null, null, null, null, 0);
        ResponseDTO<OpportunityDTO> expectedResponse = new ResponseDTO<>("Oportunidade excluída com sucesso", mockOpportunityDTO);

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        doNothing().when(opportunityRepository).delete(testOpportunity);
        when(opportunityMapper.toResponseDTO(anyString(), any(Opportunity.class))).thenReturn(expectedResponse);

        ResponseDTO<OpportunityDTO> result = adminReportService.deleteOpportunityByAdmin(1L);

        assertNotNull(result);
        assertEquals("Oportunidade excluída com sucesso", result.message());

        verify(opportunityRepository).findById(1L);
        verify(opportunityRepository).delete(testOpportunity);
    }

    // DISMISS REPORTS TESTS

    @Test
    @DisplayName("Should dismiss post reports successfully")
    void dismissPostReportsSuccess() {
        List<Report> reports = Arrays.asList(testReport);

        when(reportRepository.findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING))
                .thenReturn(reports);
        when(reportRepository.saveAll(anyList())).thenReturn(reports);

        ResponseDTO<String> result = adminReportService.dismissReports(1L, "POST");

        assertNotNull(result);
        assertTrue(result.message().contains("Denúncias descartadas"));

        verify(reportRepository).findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING);
        verify(reportRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should handle dismiss with no pending reports")
    void dismissReportsNoPending() {
        when(reportRepository.findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING))
                .thenReturn(Arrays.asList());

        ResponseDTO<String> result = adminReportService.dismissReports(1L, "POST");

        assertNotNull(result);

        verify(reportRepository).findByEntityIdAndTypeAndStatus(1L, Report.ReportType.POST, Report.ReportStatus.PENDING);
        verify(reportRepository).saveAll(anyList());
    }
}

