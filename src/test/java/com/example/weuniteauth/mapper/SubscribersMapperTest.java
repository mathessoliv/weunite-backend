package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.opportunity.Opportunity;
import com.example.weuniteauth.domain.opportunity.Subscriber;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Company;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.dto.Opportunity.SubscriberDTO;
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
@DisplayName("SubscribersMapper Tests")
class SubscribersMapperTest {

    @Autowired
    private SubscribersMapper subscribersMapper;

    private Subscriber testSubscriber;
    private Opportunity testOpportunity;
    private Athlete testAthlete;

    @BeforeEach
    void setUp() {
        Role athleteRole = new Role();
        athleteRole.setId(1L);
        athleteRole.setName("ATHLETE");

        Role companyRole = new Role();
        companyRole.setId(2L);
        companyRole.setName("COMPANY");

        testAthlete = new Athlete();
        testAthlete.setId(1L);
        testAthlete.setUsername("athlete");
        testAthlete.setEmail("athlete@test.com");
        testAthlete.setName("Test Athlete");

        Set<Role> athleteRoles = new HashSet<>();
        athleteRoles.add(athleteRole);
        testAthlete.setRole(athleteRoles);
        testAthlete.setCreatedAt(Instant.now());

        Company testCompany = new Company();
        testCompany.setId(2L);
        testCompany.setUsername("company");
        testCompany.setEmail("company@test.com");
        testCompany.setName("Test Company");

        Set<Role> companyRoles = new HashSet<>();
        companyRoles.add(companyRole);
        testCompany.setRole(companyRoles);
        testCompany.setCreatedAt(Instant.now());

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Software Developer");
        testOpportunity.setDescription("Great opportunity");
        testOpportunity.setLocation("Remote");
        testOpportunity.setDateEnd(LocalDate.now().plusDays(30));
        testOpportunity.setCompany(testCompany);
        testOpportunity.setCreatedAt(Instant.now());

        testSubscriber = new Subscriber();
        testSubscriber.setId(1L);
        testSubscriber.setOpportunity(testOpportunity);
        testSubscriber.setAthlete(testAthlete);
    }

    // TO SUBSCRIBER DTO TESTS

    @Test
    @DisplayName("Should convert Subscriber entity to SubscriberDTO")
    void toSubscriberDTO() {
        SubscriberDTO result = subscribersMapper.toSubscriberDTO(testSubscriber);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNotNull(result.opportunity());
        assertEquals(1L, result.opportunity().id());
        assertEquals("Software Developer", result.opportunity().title());
        assertNotNull(result.athlete());
        assertEquals("athlete", result.athlete().username());
    }

    @Test
    @DisplayName("Should map opportunity details correctly")
    void toSubscriberDTOOpportunityDetails() {
        SubscriberDTO result = subscribersMapper.toSubscriberDTO(testSubscriber);

        assertNotNull(result);
        assertNotNull(result.opportunity());
        assertEquals(1L, result.opportunity().id());
        assertEquals("Software Developer", result.opportunity().title());
        assertEquals("Great opportunity", result.opportunity().description());
        assertEquals("Remote", result.opportunity().location());
    }

    @Test
    @DisplayName("Should map athlete details correctly")
    void toSubscriberDTOAthleteDetails() {
        SubscriberDTO result = subscribersMapper.toSubscriberDTO(testSubscriber);

        assertNotNull(result);
        assertNotNull(result.athlete());
        assertEquals(1L, result.athlete().id());
        assertEquals("Test Athlete", result.athlete().name());
        assertEquals("athlete@test.com", result.athlete().email());
    }

    @Test
    @DisplayName("Should map opportunity company details")
    void toSubscriberDTOCompanyDetails() {
        SubscriberDTO result = subscribersMapper.toSubscriberDTO(testSubscriber);

        assertNotNull(result);
        assertNotNull(result.opportunity());
        assertNotNull(result.opportunity().company());
        assertEquals("company", result.opportunity().company().username());
        assertEquals("Test Company", result.opportunity().company().name());
    }

    // MAP SUBSCRIBERS TO LIST TESTS

    @Test
    @DisplayName("Should convert list of subscribers to list of DTOs")
    void mapSubscribersToList() {
        Athlete athlete2 = new Athlete();
        athlete2.setId(2L);
        athlete2.setUsername("athlete2");
        athlete2.setEmail("athlete2@test.com");
        athlete2.setName("Second Athlete");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        athlete2.setRole(roles);
        athlete2.setCreatedAt(Instant.now());

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setOpportunity(testOpportunity);
        subscriber2.setAthlete(athlete2);

        Subscriber subscriber3 = new Subscriber();
        subscriber3.setId(3L);
        subscriber3.setOpportunity(testOpportunity);
        subscriber3.setAthlete(testAthlete);

        List<Subscriber> subscribers = new ArrayList<>();
        subscribers.add(testSubscriber);
        subscribers.add(subscriber2);
        subscribers.add(subscriber3);

        List<SubscriberDTO> result = subscribersMapper.mapSubscribersToList(subscribers);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("athlete", result.get(0).athlete().username());
        assertEquals("athlete2", result.get(1).athlete().username());
        assertEquals("athlete", result.get(2).athlete().username());
    }

    @Test
    @DisplayName("Should handle empty subscriber list")
    void mapSubscribersToListEmpty() {
        List<Subscriber> subscribers = new ArrayList<>();

        List<SubscriberDTO> result = subscribersMapper.mapSubscribersToList(subscribers);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null subscriber list")
    void mapSubscribersToListNull() {
        List<SubscriberDTO> result = subscribersMapper.mapSubscribersToList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TO RESPONSE DTO TESTS

    @Test
    @DisplayName("Should create ResponseDTO with message and subscriber")
    void toResponseDTO() {
        String message = "Inscrição realizada com sucesso";

        ResponseDTO<SubscriberDTO> result = subscribersMapper.toResponseDTO(message, testSubscriber);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals(1L, result.data().id());
        assertEquals("athlete", result.data().athlete().username());
    }

    @Test
    @DisplayName("Should create ResponseDTO with all subscriber details")
    void toResponseDTOComplete() {
        String message = "Candidatura enviada";

        ResponseDTO<SubscriberDTO> result = subscribersMapper.toResponseDTO(message, testSubscriber);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertNotNull(result.data().opportunity());
        assertEquals("Software Developer", result.data().opportunity().title());
        assertNotNull(result.data().athlete());
        assertEquals("Test Athlete", result.data().athlete().name());
    }

    // EDGE CASE TESTS

    @Test
    @DisplayName("Should handle subscriber with opportunity with skills")
    void toSubscriberDTOWithSkills() {
        // Opportunity já vem sem skills no setUp, mas vamos garantir
        testOpportunity.setSkills(null);

        SubscriberDTO result = subscribersMapper.toSubscriberDTO(testSubscriber);

        assertNotNull(result);
        assertNotNull(result.opportunity());
        // Skills pode ser null ou vazio
    }

    @Test
    @DisplayName("Should handle multiple subscribers for same opportunity")
    void mapSubscribersSameOpportunity() {
        Athlete athlete2 = new Athlete();
        athlete2.setId(3L);
        athlete2.setUsername("athlete3");
        athlete2.setEmail("athlete3@test.com");
        athlete2.setName("Third Athlete");

        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        athlete2.setRole(roles);
        athlete2.setCreatedAt(Instant.now());

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setOpportunity(testOpportunity); // Mesma oportunidade
        subscriber2.setAthlete(athlete2);

        List<Subscriber> subscribers = new ArrayList<>();
        subscribers.add(testSubscriber);
        subscribers.add(subscriber2);

        List<SubscriberDTO> result = subscribersMapper.mapSubscribersToList(subscribers);

        assertNotNull(result);
        assertEquals(2, result.size());
        // Ambos têm a mesma oportunidade
        assertEquals(result.get(0).opportunity().id(), result.get(1).opportunity().id());
        // Mas atletas diferentes
        assertNotEquals(result.get(0).athlete().id(), result.get(1).athlete().id());
    }
}

