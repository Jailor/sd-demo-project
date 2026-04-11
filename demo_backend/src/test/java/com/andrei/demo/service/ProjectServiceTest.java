package com.andrei.demo.service;

import com.andrei.demo.model.Project;
import com.andrei.demo.model.ProjectCreateDTO;
import com.andrei.demo.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(UUID.randomUUID());
        project.setTitle("Project Alpha");
    }

    @Test
    void getProjects_returnsAll() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> result = projectService.getProjects();

        assertEquals(1, result.size());
        assertEquals("Project Alpha", result.get(0).getTitle());
    }

    @Test
    void getProjects_returnsEmptyList() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(projectService.getProjects().isEmpty());
    }

    @Test
    void getProjectById_existing_returnsProject() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        Project result = projectService.getProjectById(project.getId());

        assertEquals("Project Alpha", result.getTitle());
    }

    @Test
    void getProjectById_nonExistent_throwsException() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> projectService.getProjectById(id));
    }

    @Test
    void addProject_savesAndReturns() {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setTitle("New Project");

        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        Project result = projectService.addProject(dto);

        assertNotNull(result.getId());
        assertEquals("New Project", result.getTitle());
    }

    @Test
    void updateProject_existing_updatesAndReturns() {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setTitle("Updated Title");

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.updateProject(project.getId(), dto);

        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void updateProject_nonExistent_throwsException() {
        UUID id = UUID.randomUUID();
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setTitle("Whatever");
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> projectService.updateProject(id, dto));
    }

    @Test
    void deleteProject_callsRepository() {
        UUID id = UUID.randomUUID();
        projectService.deleteProject(id);
        verify(projectRepository).deleteById(id);
    }
}
