package com.example.weuniteauth.controller;

import com.example.weuniteauth.dto.SavedOpportunityDTO;
import com.example.weuniteauth.service.SavedOpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/saved-opportunities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Saved Opportunities", description = "Endpoints para gerenciar oportunidades salvas")
public class SavedOpportunityController {

    private final SavedOpportunityService savedOpportunityService;

    @PostMapping("/toggle/{athleteId}/{opportunityId}")
    @Operation(summary = "Salvar ou remover oportunidade dos favoritos")
    public ResponseEntity<Map<String, Object>> toggleSavedOpportunity(
            @PathVariable Long athleteId,
            @PathVariable Long opportunityId
    ) {
        try {
            SavedOpportunityDTO result = savedOpportunityService
                    .toggleSavedOpportunity(athleteId, opportunityId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isSaved", result != null);
            response.put("data", result);
            response.put("message", result != null
                    ? "Oportunidade salva com sucesso!"
                    : "Oportunidade removida dos salvos");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao alternar oportunidade salva", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao processar solicitação: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/athlete/{athleteId}")
    @Operation(summary = "Listar todas as oportunidades salvas de um atleta")
    public ResponseEntity<Map<String, Object>> getSavedOpportunities(
            @PathVariable Long athleteId
    ) {
        try {
            List<SavedOpportunityDTO> savedOpportunities = savedOpportunityService
                    .getSavedOpportunitiesByAthlete(athleteId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedOpportunities);
            response.put("message", "Oportunidades salvas carregadas com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao buscar oportunidades salvas", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao buscar oportunidades salvas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/isSaved/{athleteId}/{opportunityId}")
    @Operation(summary = "Verificar se uma oportunidade está salva")
    public ResponseEntity<Map<String, Object>> isSaved(
            @PathVariable Long athleteId,
            @PathVariable Long opportunityId
    ) {
        try {
            boolean isSaved = savedOpportunityService.isSaved(athleteId, opportunityId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", isSaved);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao verificar se oportunidade está salva", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao verificar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

