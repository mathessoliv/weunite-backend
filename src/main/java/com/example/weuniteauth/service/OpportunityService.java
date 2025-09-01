package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.Opportunity;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.exceptions.UnauthorizedException;
import com.example.weuniteauth.exceptions.opportunity.OpportunityNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.OpportunityMapper;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpportunityService {

    private final UserRepository userRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;

    public OpportunityService(UserRepository userRepository, OpportunityRepository opportunityRepository, OpportunityMapper opportunityMapper) {
        this.userRepository = userRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityMapper = opportunityMapper;
    }

    @Transactional
    public ResponseDTO<OpportunityDTO> createOpportunity(Long userId, OpportunityDTO opportunityDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Opportunity createdOpportunity = new Opportunity(
                user,
                opportunityDTO.title(),
                opportunityDTO.description(),
                opportunityDTO.location(),
                opportunityDTO.dateEnd(),
                opportunityDTO.skills()
        );

        opportunityRepository.save(createdOpportunity);

        return opportunityMapper.toResponseDTO("Oportunidade criada com sucesso!", createdOpportunity);
    }

    @Transactional
    public ResponseDTO<OpportunityDTO> updateOpportunity(Long userId, Long opportunityId, OpportunityDTO updatedOpportunityDTO) {
        Opportunity existingOpportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        if (!userId.equals(existingOpportunity.getUser().getId())) {
            throw new UnauthorizedException("Você não possui autorização para atualizar esta oportunidade.");
        }

        Opportunity updatedOpportunity = opportunityMapper.toEntity(updatedOpportunityDTO);

        opportunityRepository.save(updatedOpportunity);

        return opportunityMapper.toResponseDTO("Oportunidade atualizada com sucesso!", updatedOpportunity);
    }

    @Transactional
    public ResponseDTO<OpportunityDTO> deleteOpportunity(Long userId, Long opportunityId) {
        Opportunity existingOpportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        if (!userId.equals(existingOpportunity.getUser().getId())) {
            throw new UnauthorizedException("Você não possui autorização para deletar esta oportunidade.");
        }

        opportunityRepository.delete(existingOpportunity);

        return opportunityMapper.toResponseDTO("Oportunidade deletada com sucesso!", existingOpportunity);

    }

    @Transactional
    public ResponseDTO<OpportunityDTO> getOpportunity(Long opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        return opportunityMapper.toResponseDTO("Oportunidade encontrada com sucesso!", opportunity);

    }

    @Transactional
    public List<OpportunityDTO> getOpportunities() {

        List<Opportunity> opportunities = opportunityRepository.findAllOrderedByCreationDate();

        return opportunityMapper.toOpportunityDTOList(opportunities);
    }

    @Transactional
    public List<OpportunityDTO> getOpportunitiesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Opportunity> opportunities = opportunityRepository.findByUserId(userId);
        return opportunityMapper.toOpportunityDTOList(opportunities);
    }

}
