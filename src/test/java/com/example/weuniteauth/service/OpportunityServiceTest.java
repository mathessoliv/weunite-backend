package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Opportunity;
import com.example.weuniteauth.domain.Skills;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.exceptions.UnauthorizedException;
import com.example.weuniteauth.exceptions.opportunity.OpportunityNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.OpportunityMapper;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpportunityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private OpportunityMapper opportunityMapper;

    @InjectMocks
    private OpportunityService opportunityService;

    // CREATE OPPORTUNITY TESTS

    @Test
    @DisplayName("Should create opportunity successfully when user exists and data is valid")
    void createOpportunitySuccess() {
        Long userId = 1L;
        Set<Skills> skills = new HashSet<>();
        skills.add(new Skills("Java"));

        OpportunityDTO opportunityDTO = new OpportunityDTO(
                null,
                "Software Developer",
                "Desenvolvedor Java Sênior",
                "São Paulo, SP",
                LocalDate.of(2025, 12, 31),
                skills,
                null,
                null
        );

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        Opportunity createdOpportunity = new Opportunity(
                mockUser,
                opportunityDTO.title(),
                opportunityDTO.description(),
                opportunityDTO.location(),
                opportunityDTO.dateEnd(),
                opportunityDTO.skills()
        );
        createdOpportunity.setId(1L);
        createdOpportunity.setCreatedAt(Instant.now());

        ResponseDTO<OpportunityDTO> expectedResponse = new ResponseDTO<>(
                "Oportunidade criada com sucesso!",
                new OpportunityDTO(
                        1L,
                        "Software Developer",
                        "Desenvolvedor Java Sênior",
                        "São Paulo, SP",
                        LocalDate.of(2025, 12, 31),
                        skills,
                        Instant.now(),
                        null
                )
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(createdOpportunity);
        when(opportunityMapper.toResponseDTO(eq("Oportunidade criada com sucesso!"), any(Opportunity.class)))
                .thenReturn(expectedResponse);

        ResponseDTO<OpportunityDTO> result = opportunityService.createOpportunity(userId, opportunityDTO);

        assertNotNull(result);
        assertEquals("Oportunidade criada com sucesso!", result.message());
        assertNotNull(result.data());
        assertEquals("Software Developer", result.data().title());
        assertEquals("Desenvolvedor Java Sênior", result.data().description());

        verify(userRepository).findById(userId);
        verify(opportunityRepository).save(any(Opportunity.class));
        verify(opportunityMapper).toResponseDTO(eq("Oportunidade criada com sucesso!"), any(Opportunity.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist during creation")
    void createOpportunityWithNonExistentUser() {
        Long userId = 999L;
        OpportunityDTO opportunityDTO = new OpportunityDTO(
                null,
                "Software Developer",
                "Desenvolvedor Java Sênior",
                "São Paulo, SP",
                LocalDate.of(2025, 12, 31),
                new HashSet<>(),
                null,
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
            opportunityService.createOpportunity(userId, opportunityDTO)
        );

        assertNotNull(exception);
        verify(userRepository).findById(userId);
        verifyNoInteractions(opportunityRepository, opportunityMapper);
    }

    // UPDATE OPPORTUNITY TESTS

    @Test
    @DisplayName("Should update opportunity successfully when user is owner and data is valid")
    void updateOpportunitySuccess() {
        Long userId = 1L;
        Long opportunityId = 1L;
        Set<Skills> updatedSkills = new HashSet<>();
        updatedSkills.add(new Skills("Python"));

        OpportunityDTO updatedOpportunityDTO = new OpportunityDTO(
                opportunityId,
                "Senior Software Developer",
                "Desenvolvedor Python Sênior",
                "Rio de Janeiro, RJ",
                LocalDate.of(2025, 11, 30),
                updatedSkills,
                null,
                null
        );

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");

        Opportunity existingOpportunity = new Opportunity();
        existingOpportunity.setId(opportunityId);
        existingOpportunity.setUser(mockUser);
        existingOpportunity.setTitle("Software Developer");

        Opportunity updatedOpportunity = new Opportunity();
        updatedOpportunity.setId(opportunityId);
        updatedOpportunity.setTitle("Senior Software Developer");
        updatedOpportunity.setUpdatedAt(Instant.now());

        ResponseDTO<OpportunityDTO> expectedResponse = new ResponseDTO<>(
                "Oportunidade atualizada com sucesso!",
                updatedOpportunityDTO
        );

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(existingOpportunity));
        when(opportunityMapper.toEntity(updatedOpportunityDTO)).thenReturn(updatedOpportunity);
        when(opportunityRepository.save(updatedOpportunity)).thenReturn(updatedOpportunity);
        when(opportunityMapper.toResponseDTO(eq("Oportunidade atualizada com sucesso!"), eq(updatedOpportunity)))
                .thenReturn(expectedResponse);

        ResponseDTO<OpportunityDTO> result = opportunityService.updateOpportunity(userId, opportunityId, updatedOpportunityDTO);

        assertNotNull(result);
        assertEquals("Oportunidade atualizada com sucesso!", result.message());
        assertNotNull(result.data());

        verify(opportunityRepository).findById(opportunityId);
        verify(opportunityMapper).toEntity(updatedOpportunityDTO);
        verify(opportunityRepository).save(updatedOpportunity);
        verify(opportunityMapper).toResponseDTO(eq("Oportunidade atualizada com sucesso!"), eq(updatedOpportunity));
    }

    @Test
    @DisplayName("Should throw OpportunityNotFoundException when opportunity does not exist during update")
    void updateOpportunityWithNonExistentOpportunity() {
        Long userId = 1L;
        Long opportunityId = 999L;
        OpportunityDTO updatedOpportunityDTO = new OpportunityDTO(
                opportunityId,
                "Senior Software Developer",
                "Desenvolvedor Python Sênior",
                "Rio de Janeiro, RJ",
                LocalDate.of(2025, 11, 30),
                new HashSet<>(),
                null,
                null
        );

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.empty());

        OpportunityNotFoundException exception = assertThrows(OpportunityNotFoundException.class, () ->
            opportunityService.updateOpportunity(userId, opportunityId, updatedOpportunityDTO)
        );

        assertNotNull(exception);
        verify(opportunityRepository).findById(opportunityId);
        verifyNoInteractions(opportunityMapper);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not the owner during update")
    void updateOpportunityWithUnauthorizedUser() {
        Long userId = 1L;
        Long ownerId = 2L;
        Long opportunityId = 1L;

        OpportunityDTO updatedOpportunityDTO = new OpportunityDTO(
                opportunityId,
                "Senior Software Developer",
                "Desenvolvedor Python Sênior",
                "Rio de Janeiro, RJ",
                LocalDate.of(2025, 11, 30),
                new HashSet<>(),
                null,
                null
        );

        User opportunityOwner = new User();
        opportunityOwner.setId(ownerId);
        opportunityOwner.setUsername("owner");

        Opportunity existingOpportunity = new Opportunity();
        existingOpportunity.setId(opportunityId);
        existingOpportunity.setUser(opportunityOwner);

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(existingOpportunity));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
            opportunityService.updateOpportunity(userId, opportunityId, updatedOpportunityDTO)
        );

        assertEquals("Você não possui autorização para atualizar esta oportunidade.", exception.getMessage());
        verify(opportunityRepository).findById(opportunityId);
        verifyNoInteractions(opportunityMapper);
    }

    // DELETE OPPORTUNITY TESTS

    @Test
    @DisplayName("Should delete opportunity successfully when user is owner")
    void deleteOpportunitySuccess() {
        Long userId = 1L;
        Long opportunityId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");

        Opportunity existingOpportunity = new Opportunity();
        existingOpportunity.setId(opportunityId);
        existingOpportunity.setUser(mockUser);
        existingOpportunity.setTitle("Software Developer");

        ResponseDTO<OpportunityDTO> expectedResponse = new ResponseDTO<>(
                "Oportunidade deletada com sucesso!",
                new OpportunityDTO(
                        opportunityId,
                        "Software Developer",
                        "Desenvolvedor Java",
                        "São Paulo, SP",
                        LocalDate.of(2025, 12, 31),
                        new HashSet<>(),
                        Instant.now(),
                        null
                )
        );

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(existingOpportunity));
        when(opportunityMapper.toResponseDTO(eq("Oportunidade deletada com sucesso!"), eq(existingOpportunity)))
                .thenReturn(expectedResponse);

        ResponseDTO<OpportunityDTO> result = opportunityService.deleteOpportunity(userId, opportunityId);

        assertNotNull(result);
        assertEquals("Oportunidade deletada com sucesso!", result.message());
        assertNotNull(result.data());

        verify(opportunityRepository).findById(opportunityId);
        verify(opportunityRepository).delete(existingOpportunity);
        verify(opportunityMapper).toResponseDTO(eq("Oportunidade deletada com sucesso!"), eq(existingOpportunity));
    }

    @Test
    @DisplayName("Should throw OpportunityNotFoundException when opportunity does not exist during deletion")
    void deleteOpportunityWithNonExistentOpportunity() {
        Long userId = 1L;
        Long opportunityId = 999L;

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.empty());

        OpportunityNotFoundException exception = assertThrows(OpportunityNotFoundException.class, () ->
            opportunityService.deleteOpportunity(userId, opportunityId)
        );

        assertNotNull(exception);
        verify(opportunityRepository).findById(opportunityId);
        verify(opportunityRepository, never()).delete(any());
        verifyNoInteractions(opportunityMapper);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not the owner during deletion")
    void deleteOpportunityWithUnauthorizedUser() {
        Long userId = 1L;
        Long ownerId = 2L;
        Long opportunityId = 1L;

        User opportunityOwner = new User();
        opportunityOwner.setId(ownerId);
        opportunityOwner.setUsername("owner");

        Opportunity existingOpportunity = new Opportunity();
        existingOpportunity.setId(opportunityId);
        existingOpportunity.setUser(opportunityOwner);

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(existingOpportunity));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
            opportunityService.deleteOpportunity(userId, opportunityId)
        );

        assertEquals("Você não possui autorização para deletar esta oportunidade.", exception.getMessage());
        verify(opportunityRepository).findById(opportunityId);
        verify(opportunityRepository, never()).delete(any());
        verifyNoInteractions(opportunityMapper);
    }
}
