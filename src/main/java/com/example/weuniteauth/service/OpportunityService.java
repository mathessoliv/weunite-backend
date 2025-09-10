package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.users.Company;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.Opportunity.OpportunityRequestDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.exceptions.UnauthorizedException;
import com.example.weuniteauth.exceptions.opportunity.OpportunityNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.OpportunityMapper;
import com.example.weuniteauth.repository.CompanyRepository;
import com.example.weuniteauth.repository.OpportunityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpportunityService {

    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;

    public OpportunityService(CompanyRepository companyRepository, OpportunityRepository opportunityRepository, OpportunityMapper opportunityMapper) {
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityMapper = opportunityMapper;
    }

    @Transactional
    public ResponseDTO<OpportunityDTO> createOpportunity(Long companyId, OpportunityRequestDTO opportunityDTO) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(UserNotFoundException::new);

        Opportunity createdOpportunity = new Opportunity(
                company,
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

        if (!userId.equals(existingOpportunity.getCompany().getId())) {
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

        if (!userId.equals(existingOpportunity.getCompany().getId())) {
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
        User user = companyRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Opportunity> opportunities = opportunityRepository.findByCompanyId(userId);
        return opportunityMapper.toOpportunityDTOList(opportunities);
    }

}
