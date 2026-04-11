package com.andrei.demo.service;

import com.andrei.demo.model.Skill;
import com.andrei.demo.model.SkillCreateDTO;
import com.andrei.demo.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(UUID.randomUUID());
        skill.setName("Java");
    }

    @Test
    void getSkills_returnsAll() {
        when(skillRepository.findAll()).thenReturn(List.of(skill));

        List<Skill> result = skillService.getSkills();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
    }

    @Test
    void getSkills_returnsEmptyList() {
        when(skillRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(skillService.getSkills().isEmpty());
    }

    @Test
    void getSkillById_existing_returnsSkill() {
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));

        Skill result = skillService.getSkillById(skill.getId());

        assertEquals("Java", result.getName());
    }

    @Test
    void getSkillById_nonExistent_throwsException() {
        UUID id = UUID.randomUUID();
        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> skillService.getSkillById(id));
    }

    @Test
    void addSkill_savesAndReturns() {
        SkillCreateDTO dto = new SkillCreateDTO();
        dto.setName("Python");

        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> {
            Skill s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        Skill result = skillService.addSkill(dto);

        assertNotNull(result.getId());
        assertEquals("Python", result.getName());
    }

    @Test
    void updateSkill_existing_updatesAndReturns() {
        SkillCreateDTO dto = new SkillCreateDTO();
        dto.setName("Updated Java");

        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));

        Skill result = skillService.updateSkill(skill.getId(), dto);

        assertEquals("Updated Java", result.getName());
    }

    @Test
    void updateSkill_nonExistent_throwsException() {
        UUID id = UUID.randomUUID();
        SkillCreateDTO dto = new SkillCreateDTO();
        dto.setName("Whatever");
        when(skillRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> skillService.updateSkill(id, dto));
    }

    @Test
    void deleteSkill_callsRepository() {
        UUID id = UUID.randomUUID();
        skillService.deleteSkill(id);
        verify(skillRepository).deleteById(id);
    }
}
