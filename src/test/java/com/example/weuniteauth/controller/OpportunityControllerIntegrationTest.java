package com.example.weuniteauth.controller;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.opportunity.Skill;
import com.example.weuniteauth.domain.users.Company;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.dto.Opportunity.OpportunityRequestDTO;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.repository.CompanyRepository;
import com.example.weuniteauth.repository.OpportunityRepository;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.SkillRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OpportunityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Company company;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        company = persistCompany();
    }

    @Test
    void createOpportunityShouldPersistAndReturnResponse() throws Exception {
        OpportunityRequestDTO requestDTO = new OpportunityRequestDTO(
                "Backend Developer",
                "Own the API layer for our marketplace",
                "Remote",
                LocalDate.now().plusDays(7),
                buildSkills("Java", "Spring Boot")
        );

        MockMultipartFile opportunityPart = new MockMultipartFile(
                "opportunity",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDTO)
        );

        mockMvc.perform(
                        multipart("/api/opportunities/create/{companyId}", company.getId())
                                .file(opportunityPart)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Oportunidade criada com sucesso!"))
                .andExpect(jsonPath("$.data.title").value("Backend Developer"))
                .andExpect(jsonPath("$.data.skills", hasSize(2)));

        assertThat(opportunityRepository.findAll()).hasSize(1);
        assertThat(skillRepository.count()).isEqualTo(2);
    }

    @Test
    void updateOpportunityShouldReplaceFieldsAndSkills() throws Exception {
        Opportunity opportunity = persistOpportunity("Legacy Systems Specialist");

        OpportunityDTO updateRequest = new OpportunityDTO(
                opportunity.getId(),
                "Realtime Platform Engineer",
                "Design data streaming APIs and evolve our infra",
                "Hybrid",
                LocalDate.now().plusDays(30),
                buildSkills("Kotlin", "Kafka"),
                null,
                null,
                null
        );

        mockMvc.perform(
                        put("/api/opportunities/update/{companyId}/{opportunityId}",
                                company.getId(),
                                opportunity.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Oportunidade atualizada com sucesso!"))
                .andExpect(jsonPath("$.data.title").value("Realtime Platform Engineer"))
                .andExpect(jsonPath("$.data.location").value("Hybrid"));

        Opportunity refreshed = opportunityRepository.findById(opportunity.getId()).orElseThrow();
        assertThat(refreshed.getDescription()).isEqualTo("Design data streaming APIs and evolve our infra");
        assertThat(refreshed.getSkills())
                .extracting(Skill::getName)
                .containsExactlyInAnyOrder("Kotlin", "Kafka");
    }

    @Test
    void getOpportunityShouldReturnPersistedData() throws Exception {
        Opportunity opportunity = persistOpportunity("Analytics Lead");

        mockMvc.perform(get("/api/opportunities/get/{opportunityId}", opportunity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Analytics Lead"))
                .andExpect(jsonPath("$.data.company.username").value(company.getUsername()))
                .andExpect(jsonPath("$.data.skills[*].name", containsInAnyOrder("SQL")));
    }

    @Test
    void getOpportunitiesShouldReturnAllSavedOpportunities() throws Exception {
        persistOpportunity("Mobile Engineer");
        persistOpportunity("QA Automation Expert");

        mockMvc.perform(get("/api/opportunities/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    void deleteOpportunityShouldRemoveEntity() throws Exception {
        Opportunity opportunity = persistOpportunity("Temporary Opportunity");

        mockMvc.perform(
                        delete("/api/opportunities/delete/{companyId}/{opportunityId}",
                                company.getId(),
                                opportunity.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Oportunidade deletada com sucesso!"));

        assertThat(opportunityRepository.existsById(opportunity.getId())).isFalse();
    }

    private Company persistCompany() {
        Company newCompany = new Company(
                "Future Sports Labs",
                "future_sports_" + System.nanoTime(),
                "company+" + System.nanoTime() + "@test.com",
                "secret123"
        );
        newCompany.setEmailVerified(true);

        Role companyRole = roleRepository.findByName("COMPANY");
        if (companyRole == null) {
            companyRole = new Role();
            companyRole.setName("COMPANY");
            companyRole = roleRepository.save(companyRole);
        }

        newCompany.setRole(new HashSet<>(Collections.singleton(companyRole)));
        return companyRepository.save(newCompany);
    }

    private Opportunity persistOpportunity(String title) {
        Opportunity opportunity = new Opportunity(
                company,
                title,
                "Initial description",
                "Remote",
                LocalDate.now().plusDays(5),
                new HashSet<>(resolveSkills("SQL"))
        );
        return opportunityRepository.save(opportunity);
    }

    private Set<Skill> buildSkills(String... names) {
        return Arrays.stream(names)
                .map(Skill::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Skill> resolveSkills(String... names) {
        return Arrays.stream(names)
                .map(name -> {
                    Skill existing = skillRepository.findByName(name);
                    if (existing != null) {
                        return existing;
                    }
                    Skill created = new Skill(name);
                    return skillRepository.save(created);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
