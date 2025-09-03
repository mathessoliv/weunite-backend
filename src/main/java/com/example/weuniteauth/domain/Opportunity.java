package com.example.weuniteauth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Opportunity {

    public Opportunity(User user, String title, String description, String location, LocalDate date_end, Set<Skills> skills) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.location = location;
        this.dateEnd = date_end;
        this.skills = skills;
    }

    public Opportunity(User user, String title, String description, String location, LocalDate date_end, Set<Skills> skills, Set<Subscribers> subscribers) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.location = location;
        this.dateEnd = null;
        this.skills = skills;
        this.subscribers = subscribers;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 200)
    private String location;

    @Column(nullable = false)
    private LocalDate dateEnd;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }


    public void addSkill(Skills skill) {
        skills.add(skill);
        skill.getOpportunities().add(this);
    }

    public void removeSkill(Skills skill) {
        skills.remove(skill);
        skill.getOpportunities().remove(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "opportunity_skills",
            joinColumns = @JoinColumn(name = "opportunity_id"),
            inverseJoinColumns = @JoinColumn(name = "skills_id")
    )
    private Set<Skills> skills = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "opportunity_subscribers",
            joinColumns = @JoinColumn(name = "opportunity_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    private Set<Subscribers> subscribers = new HashSet<>();

}
