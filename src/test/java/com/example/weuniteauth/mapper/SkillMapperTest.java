package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.opportunity.Skill;
import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.SkillDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SkillMapper Tests")
class SkillMapperTest {

    @Autowired
    private SkillMapper skillMapper;

    private Skill testSkill;

    @BeforeEach
    void setUp() {
        testSkill = new Skill();
        testSkill.setId(1L);
        testSkill.setName("Java");
    }

    @Test
    @DisplayName("Should convert Skill entity to SkillDTO")
    void toSkillDTO() {
        SkillDTO result = skillMapper.toSkillDTO(testSkill);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Java", result.name());
    }

    @Test
    @DisplayName("Should convert skill with different name")
    void toSkillDTODifferentName() {
        testSkill.setName("Python");

        SkillDTO result = skillMapper.toSkillDTO(testSkill);

        assertNotNull(result);
        assertEquals("Python", result.name());
    }

    @Test
    @DisplayName("Should convert list of skills to list of DTOs")
    void toSkillDTOList() {
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setName("Spring Boot");

        Skill skill3 = new Skill();
        skill3.setId(3L);
        skill3.setName("React");

        List<Skill> skills = new ArrayList<>();
        skills.add(testSkill);
        skills.add(skill2);
        skills.add(skill3);

        List<SkillDTO> result = skillMapper.toSkillDTOList(skills);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Java", result.get(0).name());
        assertEquals("Spring Boot", result.get(1).name());
        assertEquals("React", result.get(2).name());
    }

    @Test
    @DisplayName("Should handle empty skill list")
    void toSkillDTOListEmpty() {
        List<Skill> skills = new ArrayList<>();

        List<SkillDTO> result = skillMapper.toSkillDTOList(skills);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null skill list")
    void toSkillDTOListNull() {
        List<SkillDTO> result = skillMapper.toSkillDTOList((List<Skill>) null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should convert set of skills to list of DTOs")
    void toSkillDTOListFromSet() {
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setName("Docker");

        Set<Skill> skills = new HashSet<>();
        skills.add(testSkill);
        skills.add(skill2);

        List<SkillDTO> result = skillMapper.toSkillDTOList(skills);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should create ResponseDTO with message and skill")
    void toResponseDTO() {
        String message = "Skill criada com sucesso";

        ResponseDTO<SkillDTO> result = skillMapper.toResponseDTO(message, testSkill);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertNotNull(result.data());
        assertEquals("Java", result.data().name());
    }
}

