package com.andrei.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillCreateDTO {
    @NotBlank(message = "Skill name is required")
    private String name;
}