package com.andrei.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentCreateDTO {
    @NotBlank(message = "Department name is required")
    private String name;
}