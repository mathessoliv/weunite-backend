package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.opportunity.SavedOpportunity;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.SavedOpportunityDTO;
import com.example.weuniteauth.exceptions.DuplicateResourceException;
import com.example.weuniteauth.exceptions.NotFoundResourceException;
import com.example.weuniteauth.mapper.SavedOpportunityMapper;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.SavedOpportunityRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedOpportunityService {

    private final SavedOpportunityRepository savedOpportunityRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final SavedOpportunityMapper savedOpportunityMapper;

    /**
     * Salva ou remove uma oportunidade dos favoritos do atleta (toggle)
     */
    @Transactional
    public SavedOpportunityDTO toggleSavedOpportunity(Long athleteId, Long opportunityId) {
        log.info("Toggling saved opportunity for athlete {} and opportunity {}", athleteId, opportunityId);

        // Verificar se já está salva
        Optional<SavedOpportunity> existing = savedOpportunityRepository
                .findByAthleteIdAndOpportunityId(athleteId, opportunityId);

        if (existing.isPresent()) {
            // Se já está salva, remover
            log.info("Opportunity {} is already saved by athlete {}. Removing...", opportunityId, athleteId);
            savedOpportunityRepository.delete(existing.get());
            return null; // Retorna null para indicar que foi removida
        } else {
            // Se não está salva, adicionar
            log.info("Saving opportunity {} for athlete {}", opportunityId, athleteId);

            // Buscar atleta
            User athlete = userRepository.findById(athleteId)
                    .orElseThrow(() -> new NotFoundResourceException("Atleta não encontrado"));

            // Buscar oportunidade
            Opportunity opportunity = opportunityRepository.findById(opportunityId)
                    .orElseThrow(() -> new NotFoundResourceException("Oportunidade não encontrada"));

            // Criar novo SavedOpportunity
            SavedOpportunity savedOpportunity = SavedOpportunity.builder()
                    .athlete(athlete)
                    .opportunity(opportunity)
                    .savedAt(Instant.now())
                    .build();

            SavedOpportunity saved = savedOpportunityRepository.save(savedOpportunity);
            log.info("Opportunity {} saved successfully for athlete {}", opportunityId, athleteId);

            return savedOpportunityMapper.toDTO(saved);
        }
    }

    /**
     * Busca todas as oportunidades salvas por um atleta
     */
    @Transactional(readOnly = true)
    public List<SavedOpportunityDTO> getSavedOpportunitiesByAthlete(Long athleteId) {
        log.info("Fetching all saved opportunities for athlete {}", athleteId);

        List<SavedOpportunity> savedOpportunities = savedOpportunityRepository
                .findByAthleteIdOrderBySavedAtDesc(athleteId);

        log.info("Found {} saved opportunities for athlete {}", savedOpportunities.size(), athleteId);

        return savedOpportunities.stream()
                .map(savedOpportunityMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica se uma oportunidade está salva por um atleta
     */
    @Transactional(readOnly = true)
    public boolean isSaved(Long athleteId, Long opportunityId) {
        log.debug("Checking if opportunity {} is saved by athlete {}", opportunityId, athleteId);

        boolean exists = savedOpportunityRepository.existsByAthleteIdAndOpportunityId(athleteId, opportunityId);

        log.debug("Opportunity {} is {} by athlete {}",
                opportunityId,
                exists ? "saved" : "not saved",
                athleteId);

        return exists;
    }

    /**
     * Remove uma oportunidade salva específica
     */
    @Transactional
    public void removeSavedOpportunity(Long athleteId, Long opportunityId) {
        log.info("Removing saved opportunity {} for athlete {}", opportunityId, athleteId);

        SavedOpportunity savedOpportunity = savedOpportunityRepository
                .findByAthleteIdAndOpportunityId(athleteId, opportunityId)
                .orElseThrow(() -> new NotFoundResourceException("Oportunidade salva não encontrada"));

        savedOpportunityRepository.delete(savedOpportunity);
        log.info("Saved opportunity removed successfully");
    }

    /**
     * Remove todas as oportunidades salvas de um atleta
     */
    @Transactional
    public void removeAllSavedOpportunitiesByAthlete(Long athleteId) {
        log.info("Removing all saved opportunities for athlete {}", athleteId);

        int deleted = savedOpportunityRepository.deleteByAthleteId(athleteId);

        log.info("{} saved opportunities removed for athlete {}", deleted, athleteId);
    }

    /**
     * Remove todos os saves de uma oportunidade específica
     * (útil quando uma oportunidade é deletada)
     */
    @Transactional
    public void removeAllSavesForOpportunity(Long opportunityId) {
        log.info("Removing all saves for opportunity {}", opportunityId);

        int deleted = savedOpportunityRepository.deleteByOpportunityId(opportunityId);

        log.info("{} saves removed for opportunity {}", deleted, opportunityId);
    }
}

