package com.andrei.demo.service;

import com.andrei.demo.model.Skill;
import com.andrei.demo.model.SkillCreateDTO;
import com.andrei.demo.repository.SkillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public List<Skill> getSkills() {
        return skillRepository.findAll();
    }

    public Skill getSkillById(UUID id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Skill not found"));
    }

    public Skill addSkill(SkillCreateDTO dto) {
        Skill skill = new Skill();
        skill.setName(dto.getName());
        return skillRepository.save(skill);
    }

    public Skill updateSkill(UUID id, SkillCreateDTO dto) {
        Skill skill = getSkillById(id);
        skill.setName(dto.getName());
        return skillRepository.save(skill);
    }

    public void deleteSkill(UUID id) {
        skillRepository.deleteById(id);
    }
}