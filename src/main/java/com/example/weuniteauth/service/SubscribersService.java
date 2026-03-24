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
    private final NotificationService notificationService;

    public SubscribersService(SubscribersRepository subscribersRepository, OpportunityRepository opportunityRepository, AthleteRepository athleteRepository, SubscribersMapper subscribersMapper, NotificationService notificationService) {
        this.opportunityRepository = opportunityRepository;
        this.subscribersRepository = subscribersRepository;
        this.athleteRepository = athleteRepository;
        this.subscribersMapper = subscribersMapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public ResponseDTO<SubscriberDTO> toggleSubscriber(Long athleteId, Long opportunityId) {

        Athlete athlete = athleteRepository.findById(athleteId).
                orElseThrow(UserNotFoundException::new);

        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(OpportunityNotFoundException::new);

        Subscriber existingSubscriber = subscribersRepository.findByAthleteAndOpportunity(athlete, opportunity)
                .orElse(null);

        if (existingSubscriber == null) {
            Subscriber newSubscriber = new Subscriber(athlete, opportunity);
            opportunity.addSubscriber(newSubscriber);
            subscribersRepository.save(newSubscriber);
            opportunityRepository.save(opportunity);

            notificationService.createNotification(
                    opportunity.getCompany().getId(),
                    "OPPORTUNITY_SUBSCRIPTION",
                    athleteId,
                    opportunityId,
                    null
            );

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

        List<Subscriber> subscribers = subscribersRepository.findByOpportunityIdWithAthlete(opportunityId);

        subscribers.forEach(subscriber -> {
            subscriber.getAthlete().getUsername();
            subscriber.getAthlete().getName();
            subscriber.getAthlete().getEmail();
            if (subscriber.getOpportunity() != null) {
                subscriber.getOpportunity().getTitle();
            }
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

        subscribers.forEach(subscriber -> {
            subscriber.getAthlete().getUsername();
            subscriber.getOpportunity().getTitle();
            subscriber.getOpportunity().getCompany().getUsername();
            subscriber.getOpportunity().getSubscribers().size();
        });

        return subscribersMapper.mapSubscribersToList(subscribers);
    }
}