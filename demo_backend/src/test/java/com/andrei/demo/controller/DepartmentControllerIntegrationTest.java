package com.andrei.demo.controller;

import com.andrei.demo.model.Department;
import com.andrei.demo.model.DepartmentCreateDTO;
import com.andrei.demo.repository.DepartmentRepository;
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
class DepartmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department savedDepartment;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();

        Department dept = new Department();
        dept.setName("Engineering");
        savedDepartment = departmentRepository.save(dept);
    }

    @Test
    void getDepartments_returnsAll() throws Exception {
        mockMvc.perform(get("/department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Engineering")));
    }

    @Test
    void getDepartmentById_existingId_returnsDepartment() throws Exception {
        mockMvc.perform(get("/department/{id}", savedDepartment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Engineering")));
    }

    @Test
    void addDepartment_validData_returnsCreated() throws Exception {
        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setName("Marketing");

        mockMvc.perform(post("/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Marketing")));

        assertEquals(2, departmentRepository.findAll().size());
    }

    @Test
    void addDepartment_blankName_returnsBadRequest() throws Exception {
        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setName("");

        mockMvc.perform(post("/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDepartment_existingId_returnsUpdated() throws Exception {
        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setName("Updated Engineering");

        mockMvc.perform(put("/department/{id}", savedDepartment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Engineering")));
    }

    @Test
    void deleteDepartment_existingId_deletesSuccessfully() throws Exception {
        mockMvc.perform(delete("/department/{id}", savedDepartment.getId()))
                .andExpect(status().isOk());

        assertTrue(departmentRepository.findById(savedDepartment.getId()).isEmpty());
    }

    @Test
    void getDepartmentById_nonExistentId_returnsError() throws Exception {
        mockMvc.perform(get("/department/{id}", UUID.randomUUID()))
                .andExpect(status().is5xxServerError());
    }
}
