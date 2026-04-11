package com.andrei.demo.controller;

import com.andrei.demo.model.Project;
import com.andrei.demo.model.ProjectCreateDTO;
import com.andrei.demo.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    public Project addProject(@Valid @RequestBody ProjectCreateDTO dto) {
        return projectService.addProject(dto);
    }

    @PutMapping("/{id}")
    public Project updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectCreateDTO dto) {
        return projectService.updateProject(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
}