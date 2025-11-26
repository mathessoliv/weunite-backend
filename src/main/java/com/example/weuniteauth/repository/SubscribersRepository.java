package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.opportunity.Subscriber;
import com.example.weuniteauth.domain.users.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscribersRepository extends JpaRepository<Subscriber, Integer> {

    Optional<Subscriber> findByAthleteAndOpportunity(Athlete athlete, Opportunity opportunity);

    @Query("SELECT s FROM Subscriber s JOIN FETCH s.athlete WHERE s.opportunity.id = :opportunityId")
    List<Subscriber> findByOpportunityIdWithAthlete(@Param("opportunityId") Long opportunityId);

    List<Subscriber> findByOpportunityId(Long opportunityId);

    List<Subscriber> findByAthleteId(Long athleteId);
}
