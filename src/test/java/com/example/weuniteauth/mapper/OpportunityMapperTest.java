package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.opportunity.Skill;
import com.example.weuniteauth.domain.users.Company;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.dto.OpportunityDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("OpportunityMapper Tests")
class OpportunityMapperTest {

    @Autowired
    private OpportunityMapper opportunityMapper;

    private Opportunity testOpportunity;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(2L);
        role.setName("COMPANY");

        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setUsername("testcompany");
        testCompany.setEmail("company@test.com");
        testCompany.setName("Test Company");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testCompany.setRole(roles);
        testCompany.setCreatedAt(Instant.now());

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Software Developer");
        testOpportunity.setDescription("Looking for a talented developer");
        testOpportunity.setLocation("Remote");
        testOpportunity.setDateEnd(LocalDate.now().plusDays(30)); // +30 days
        testOpportunity.setCompany(testCompany);
        testOpportunity.setCreatedAt(Instant.now());
        testOpportunity.setUpdatedAt(Instant.now());
    }

    // TO OPPORTUNITY DTO TESTS

    @Test
    @DisplayName("Should convert Opportunity entity to OpportunityDTO")
    void toOpportunityDTO() {
        OpportunityDTO result = opportunityMapper.toOpportunityDTO(testOpportunity);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Software Developer", result.title());
        assertEquals("Looking for a talented developer", result.description());
        assertEquals("Remote", result.location());
        assertNotNull(result.dateEnd());
        assertNotNull(result.company());
        assertEquals("testcompany", result.company().username());
        assertNotNull(result.createdAt());
    }

    @Test
    @DisplayName("Should convert opportunity with skills")
    void toOpportunityDTOWithSkills() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setName("Java");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setName("Spring Boot");

        Set<Skill> skills = new HashSet<>();
        skills.add(skill1);
        skills.add(skill2);
        testOpportunity.setSkills(skills);

        OpportunityDTO result = opportunityMapper.toOpportunityDTO(testOpportunity);

        assertNotNull(result);
        assertNotNull(result.skills());
        assertEquals(2, result.skills().size());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void toOpportunityDTONullFields() {
        testOpportunity.setUpdatedAt(null);
        testOpportunity.setSkills(null);

        OpportunityDTO result = opportunityMapper.toOpportunityDTO(testOpportunity);

        assertNotNull(result);
        assertNull(result.updatedAt());
        assertNull(result.skills());
    }

    // TO OPPORTUNITY DTO LIST TESTS

    @Test
    @DisplayName("Should convert list of opportunities to list of DTOs")
    void toOpportunityDTOList() {
        Opportunity opportunity2 = new Opportunity();
        opportunity2.setId(2L);
        opportunity2.setTitle("Product Manager");
        opportunity2.setDescription("Lead our product team");
        opportunity2.setLocation("New York");
        opportunity2.setDateEnd(LocalDate.now().plusDays(30));
        opportunity2.setCompany(testCompany);
        opportunity2.setCreatedAt(Instant.now());

        List<Opportunity> opportunities = new ArrayList<>();
        opportunities.add(testOpportunity);
        opportunities.add(opportunity2);

        List<OpportunityDTO> result = opportunityMapper.toOpportunityDTOList(opportunities);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Developer", result.get(0).title());
        assertEquals("Product Manager", result.get(1).title());
    }

    @Test
    @DisplayName("Should handle empty opportunity list")
    void toOpportunityDTOListEmpty() {
        List<Opportunity> opportunities = new ArrayList<>();

        List<OpportunityDTO> result = opportunityMapper.toOpportunityDTOList(opportunities);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null opportunity list")
    void toOpportunityDTOListNull() {
        List<OpportunityDTO> result = opportunityMapper.toOpportunityDTOList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TO RESPONSE DTO TESTS

    @Test
    @DisplayName("Should create ResponseDTO with message and opportunity")
    void toResponseDTO() {
        String message = "Oportunidade criada com sucesso";

        ResponseDTO<OpportunityDTO> result = opportunityMapper.toResponseDTO(message, testOpportunity);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("Software Developer", result.data().title());
    }

    @Test
    @DisplayName("Should create ResponseDTO for opportunity with all fields")
    void toResponseDTOComplete() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Python");

        Set<Skill> skills = new HashSet<>();
        skills.add(skill);
        testOpportunity.setSkills(skills);

        String message = "Oportunidade atualizada";

        ResponseDTO<OpportunityDTO> result = opportunityMapper.toResponseDTO(message, testOpportunity);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertNotNull(result.data().skills());
        assertEquals(1, result.data().skills().size());
    }

    // TO ENTITY TESTS

    @Test
    @DisplayName("Should convert OpportunityDTO to Opportunity entity")
    void toEntity() {
        OpportunityDTO dto = new OpportunityDTO(
                null,
                "Backend Developer",
                "Join our backend team",
                "São Paulo",
                LocalDate.now().plusDays(30),
                null,
                Instant.now(),
                null,
                null
        );

        Opportunity result = opportunityMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Backend Developer", result.getTitle());
        assertEquals("Join our backend team", result.getDescription());
        assertEquals("São Paulo", result.getLocation());
        assertNotNull(result.getDateEnd());
    }

    @Test
    @DisplayName("Should map only specified fields to entity")
    void toEntityIgnoreByDefault() {
        OpportunityDTO dto = new OpportunityDTO(
                1L,
                "Data Scientist",
                "Work with AI",
                "Remote",
                LocalDate.now().plusDays(30),
                null,
                Instant.now(),
                Instant.now(),
                null
        );

        Opportunity result = opportunityMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Data Scientist", result.getTitle());
        // ID não deve ser mapeado (ignoreByDefault = true)
        assertNull(result.getId());
    }
}
