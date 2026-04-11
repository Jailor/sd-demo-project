package com.andrei.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProjectCreateDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title should be between 2 and 100 characters")
    private String title;

    @Data
    public static class PersonPatchDTO {

        private String name;
        private String password;
        private Integer age;
        private String email;

        private UUID departmentId;
        private List<UUID> projectIds;
    }
}