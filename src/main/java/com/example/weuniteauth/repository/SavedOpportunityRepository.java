package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.opportunity.SavedOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedOpportunityRepository extends JpaRepository<SavedOpportunity, Long> {

    /**
     * Busca uma oportunidade salva por atleta e oportunidade
     */
    @Query("SELECT s FROM SavedOpportunity s WHERE s.athlete.id = :athleteId AND s.opportunity.id = :opportunityId")
    Optional<SavedOpportunity> findByAthleteIdAndOpportunityId(
            @Param("athleteId") Long athleteId,
            @Param("opportunityId") Long opportunityId
    );

    /**
     * Busca todas as oportunidades salvas por um atleta ordenadas por data (mais recentes primeiro)
     */
    @Query("SELECT s FROM SavedOpportunity s WHERE s.athlete.id = :athleteId ORDER BY s.savedAt DESC")
    List<SavedOpportunity> findByAthleteIdOrderBySavedAtDesc(@Param("athleteId") Long athleteId);

    /**
     * Verifica se uma oportunidade está salva por um atleta
     */
    @Query("SELECT COUNT(s) > 0 FROM SavedOpportunity s WHERE s.athlete.id = :athleteId AND s.opportunity.id = :opportunityId")
    boolean existsByAthleteIdAndOpportunityId(
            @Param("athleteId") Long athleteId,
            @Param("opportunityId") Long opportunityId
    );

    /**
     * Remove uma oportunidade salva específica
     */
    void deleteByAthleteIdAndOpportunityId(Long athleteId, Long opportunityId);

    /**
     * Remove todas as oportunidades salvas de um atleta
     */
    int deleteByAthleteId(Long athleteId);

    /**
     * Remove todos os saves de uma oportunidade
     */
    int deleteByOpportunityId(Long opportunityId);
}

