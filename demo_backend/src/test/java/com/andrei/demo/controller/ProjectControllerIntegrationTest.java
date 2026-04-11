package com.andrei.demo.controller;

import com.andrei.demo.model.Project;
import com.andrei.demo.model.ProjectCreateDTO;
import com.andrei.demo.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    private Project savedProject;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();

        Project project = new Project();
        project.setTitle("Alpha");
        savedProject = projectRepository.save(project);
    }

    @Test
    void getProjects_returnsAll() throws Exception {
        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Alpha")));
    }

    @Test
    void getProjectById_existingId_returnsProject() throws Exception {
        mockMvc.perform(get("/project/{id}", savedProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Alpha")));
    }

    @Test
    void addProject_validData_returnsCreated() throws Exception {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setTitle("Beta");

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Beta")));

        assertEquals(2, projectRepository.findAll().size());
    }

    @Test
    void addProject_blankTitle_returnsBadRequest() throws Exception {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setTitle("");

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProject_existingId_returnsUpdated() throws Exception {
        ProjectCreateDTO dto = new ProjectCreateDTO();
        dto.setTitle("Updated Alpha");

        mockMvc.perform(put("/project/{id}", savedProject.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Alpha")));
    }

    @Test
    void deleteProject_existingId_deletesSuccessfully() throws Exception {
        mockMvc.perform(delete("/project/{id}", savedProject.getId()))
                .andExpect(status().isOk());

        assertTrue(projectRepository.findById(savedProject.getId()).isEmpty());
    }

    @Test
    void getProjectById_nonExistentId_returnsError() throws Exception {
        mockMvc.perform(get("/project/{id}", UUID.randomUUID()))
                .andExpect(status().is5xxServerError());
    }
}
