package com.example.weuniteauth.repository;


import com.example.weuniteauth.domain.opportunity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    @Query("SELECT o FROM Opportunity o ORDER BY COALESCE(o.updatedAt, o.createdAt) DESC")
    List<Opportunity> findAllOrderedByCreationDate();

    List<Opportunity> findByCompanyId(Long userId);

    @Query("SELECT COUNT(o) FROM Opportunity o WHERE o.createdAt >= :startDate AND o.createdAt < :endDate")
    Long countOpportunitiesBetweenDates(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT FUNCTION('MONTH', o.createdAt) as month, COUNT(o) FROM Opportunity o WHERE o.createdAt >= :startDate GROUP BY FUNCTION('MONTH', o.createdAt) ORDER BY month")
    List<Object[]> countOpportunitiesByMonth(@Param("startDate") Instant startDate);

    @Query("SELECT DISTINCT o FROM Opportunity o LEFT JOIN FETCH o.skills")
    List<Opportunity> findAllWithSkills();
}
