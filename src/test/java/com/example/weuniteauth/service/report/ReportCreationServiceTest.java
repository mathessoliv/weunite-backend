package com.example.weuniteauth.service.report;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.report.ReportDTO;
import com.example.weuniteauth.dto.report.ReportRequestDTO;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.ReportMapper;
import com.example.weuniteauth.repository.ReportRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportCreationServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportCreationService reportCreationService;

    @Test
    @DisplayName("createReport deve salvar denuncia e retornar ResponseDTO com mensagem de sucesso")
    void createReport_sucesso() {
        Long userId = 1L;
        ReportRequestDTO requestDTO = new ReportRequestDTO("POST", 10L, "SPAM");

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ReportDTO reportDTO = new ReportDTO("id-1", null, "POST", 10L, "SPAM", "PENDING", null, null, null);
        @SuppressWarnings("unchecked")
        ResponseDTO<ReportDTO> responseDTO = new ResponseDTO("Denúncia registrada com sucesso!", reportDTO);

        when(reportMapper.toResponseDTO(anyString(), any(Report.class))).thenReturn(responseDTO);

        ResponseDTO<ReportDTO> result = reportCreationService.createReport(userId, requestDTO);

        assertSame(responseDTO, result);
        verify(userRepository).findById(userId);

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());

        Report saved = reportCaptor.getValue();
        assertEquals(user, saved.getReporter());
        assertEquals(Report.ReportType.POST, saved.getType());
        assertEquals(10L, saved.getEntityId());
        assertEquals("SPAM", saved.getReason());
        verify(reportMapper).toResponseDTO("Denúncia registrada com sucesso!", saved);
    }

    @Test
    @DisplayName("createReport deve lancar UserNotFoundException quando usuario nao existir")
    void createReport_usuarioNaoEncontrado() {
        Long userId = 1L;
        ReportRequestDTO requestDTO = new ReportRequestDTO("POST", 10L, "SPAM");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> reportCreationService.createReport(userId, requestDTO));

        verify(reportRepository, never()).save(any());
        verify(reportMapper, never()).toResponseDTO(anyString(), any());
    }
}

