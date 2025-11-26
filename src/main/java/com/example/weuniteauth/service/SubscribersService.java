package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.opportunity.Subscriber;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.dto.Opportunity.SubscriberDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.exceptions.opportunity.OpportunityNotFoundException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.SubscribersMapper;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.SubscribersRepository;
import com.example.weuniteauth.repository.user.AthleteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscribersService {

    private final SubscribersRepository subscribersRepository;
    private final OpportunityRepository opportunityRepository;
    private final AthleteRepository athleteRepository;
    private final SubscribersMapper subscribersMapper;

    public SubscribersService(SubscribersRepository subscribersRepository, OpportunityRepository opportunityRepository, AthleteRepository athleteRepository, SubscribersMapper subscribersMapper) {
        this.opportunityRepository = opportunityRepository;
        this.subscribersRepository = subscribersRepository;
        this.athleteRepository = athleteRepository;
        this.subscribersMapper = subscribersMapper;
    }

    @Transactional
    public ResponseDTO<SubscriberDTO> toggleSubscriber(Long athleteId, Long opportunityId) {

        Athlete athlete = athleteRepository.findById(athleteId).
                orElseThrow(UserNotFoundException::new);

        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        // Verifica se a oportunidade já passou da data limite
        if (opportunity.getDateEnd() != null && opportunity.getDateEnd().isBefore(java.time.LocalDate.now())) {
            throw new com.example.weuniteauth.exceptions.BusinessRuleException("Prazo da oportunidade já expirou");
        }

        Subscriber existingSubscriber = subscribersRepository.findByAthleteAndOpportunity(athlete, opportunity)
                .orElse(null);

        if (existingSubscriber == null) {
            Subscriber newSubscriber = new Subscriber(athlete, opportunity);
            opportunity.addSubscriber(newSubscriber);
            subscribersRepository.save(newSubscriber);
            opportunityRepository.save(opportunity);
            return subscribersMapper.toResponseDTO("Inscrição criada com sucesso!", newSubscriber);
        } else {
            opportunity.removeSubscriber(existingSubscriber);
            subscribersRepository.delete(existingSubscriber);
            opportunityRepository.save(opportunity);
            return subscribersMapper.toResponseDTO("Inscrição removida com sucesso!", existingSubscriber);
        }
    }

    @Transactional
    public List<SubscriberDTO> getSubscribersByOpportunity(Long opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId).
                orElseThrow(OpportunityNotFoundException::new);

        List<Subscriber> subscribers = subscribersRepository.findByOpportunityId(opportunityId);

        // Forçar carregamento dos relacionamentos LAZY
        subscribers.forEach(subscriber -> {
            subscriber.getAthlete().getUsername(); // Força carregamento do athlete
            subscriber.getOpportunity().getTitle(); // Força carregamento da opportunity
            subscriber.getOpportunity().getSubscribers().size(); // Força carregamento dos subscribers da opportunity
        });

        return subscribersMapper.mapSubscribersToList(subscribers);
    }

    @Transactional(readOnly = true)
    public Boolean isSubscribed(Long athleteId, Long opportunityId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(UserNotFoundException::new);

        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        return subscribersRepository.findByAthleteAndOpportunity(athlete, opportunity).isPresent();
    }

    @Transactional(readOnly = true)
    public List<SubscriberDTO> getSubscribersByAthlete(Long athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(UserNotFoundException::new);

        List<Subscriber> subscribers = subscribersRepository.findByAthleteId(athleteId);

        // Forçar carregamento dos relacionamentos LAZY
        subscribers.forEach(subscriber -> {
            subscriber.getAthlete().getUsername(); // Força carregamento do athlete
            subscriber.getOpportunity().getTitle(); // Força carregamento da opportunity
            subscriber.getOpportunity().getCompany().getUsername(); // Força carregamento da company
            subscriber.getOpportunity().getSubscribers().size(); // Força carregamento dos subscribers da opportunity
        });

        return subscribersMapper.mapSubscribersToList(subscribers);
    }
}
