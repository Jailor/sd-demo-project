package com.andrei.demo.controller;

import com.andrei.demo.model.Skill;
import com.andrei.demo.model.SkillCreateDTO;
import com.andrei.demo.service.SkillService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/skill")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public List<Skill> getSkills() {
        return skillService.getSkills();
    }

    @GetMapping("/{id}")
    public Skill getSkillById(@PathVariable UUID id) {
        return skillService.getSkillById(id);
    }

    @PostMapping
    public Skill addSkill(@Valid @RequestBody SkillCreateDTO dto) {
        return skillService.addSkill(dto);
    }

    @PutMapping("/{id}")
    public Skill updateSkill(@PathVariable UUID id, @Valid @RequestBody SkillCreateDTO dto) {
        return skillService.updateSkill(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
    }
}