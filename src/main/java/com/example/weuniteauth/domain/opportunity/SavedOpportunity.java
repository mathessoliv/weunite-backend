package com.example.weuniteauth.domain.opportunity;

import com.example.weuniteauth.domain.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "saved_opportunities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    private User athlete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @Column(name = "saved_at", nullable = false)
    @Builder.Default
    private Instant savedAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        savedAt = Instant.now();
    }
}

