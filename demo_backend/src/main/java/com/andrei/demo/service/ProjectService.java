package com.andrei.demo.service;

import com.andrei.demo.model.Project;
import com.andrei.demo.model.ProjectCreateDTO;
import com.andrei.demo.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Project not found"));
    }

    public Project addProject(ProjectCreateDTO dto) {
        Project project = new Project();
        project.setTitle(dto.getTitle());   // <-- use title, not name
        return projectRepository.save(project);
    }

    public Project updateProject(UUID id, ProjectCreateDTO dto) {
        Project project = getProjectById(id);
        project.setTitle(dto.getTitle());   // <-- use title, not name
        return projectRepository.save(project);
    }

    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }
}