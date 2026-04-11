package com.andrei.demo.controller;

import com.andrei.demo.model.Department;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.PersonCreateDTO;
import com.andrei.demo.model.PersonPatchDTO;
import com.andrei.demo.repository.DepartmentRepository;
import com.andrei.demo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Person savedPerson;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        departmentRepository.deleteAll();

        Person person = new Person();
        person.setName("John Doe");
        person.setEmail("john@test.com");
        person.setPassword("Secure1234!");
        person.setAge(25);
        person.setRole("USER");
        savedPerson = personRepository.save(person);
    }

    @Test
    void getPeople_returnsAllPersons() throws Exception {
        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    void getPersonById_existingId_returnsPerson() throws Exception {
        mockMvc.perform(get("/person/{uuid}", savedPerson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@test.com")));
    }

    @Test
    void getPersonById_nonExistentId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/person/{uuid}", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPersonByEmail_existingEmail_returnsPerson() throws Exception {
        mockMvc.perform(get("/person/email/{email}", "john@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void addPerson_withValidData_returnsCreatedPerson() throws Exception {
        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setName("Jane Smith");
        dto.setEmail("jane@test.com");
        dto.setPassword("Strong1234!");
        dto.setAge(22);

        MvcResult result = mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.email", is("jane@test.com")))
                .andReturn();

        // Verify saved in DB
        List<Person> all = personRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void addPerson_withDuplicateEmail_returnsBadRequest() throws Exception {
        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setName("Another Person");
        dto.setEmail("john@test.com"); // already exists
        dto.setPassword("Strong1234!");
        dto.setAge(30);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPerson_withUnderageAge_returnsBadRequest() throws Exception {
        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setName("Young Person");
        dto.setEmail("young@test.com");
        dto.setPassword("Strong1234!");
        dto.setAge(16);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPerson_withPasswordContainingName_returnsBadRequest() throws Exception {
        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setName("TestUser");
        dto.setEmail("testuser@test.com");
        dto.setPassword("TestUser12!");
        dto.setAge(25);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPerson_withInvalidValidation_returnsBadRequest() throws Exception {
        PersonCreateDTO dto = new PersonCreateDTO();
        // Missing required fields

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePerson_existingId_deletesSuccessfully() throws Exception {
        mockMvc.perform(delete("/person/{uuid}", savedPerson.getId()))
                .andExpect(status().isOk());

        assertTrue(personRepository.findById(savedPerson.getId()).isEmpty());
    }

    @Test
    void patchPerson_updateName_succeeds() throws Exception {
        PersonPatchDTO dto = new PersonPatchDTO();
        dto.setName("Updated Name");

        mockMvc.perform(patch("/person/{uuid}", savedPerson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void patchPerson_updateAge_underage_returnsBadRequest() throws Exception {
        PersonPatchDTO dto = new PersonPatchDTO();
        dto.setAge(15);

        mockMvc.perform(patch("/person/{uuid}", savedPerson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchPerson_nonExistent_returnsBadRequest() throws Exception {
        PersonPatchDTO dto = new PersonPatchDTO();
        dto.setName("Does Not Matter");

        mockMvc.perform(patch("/person/{uuid}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPerson_withDepartment_associatesDepartment() throws Exception {
        Department dept = new Department();
        dept.setName("Engineering");
        dept = departmentRepository.save(dept);

        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setName("Dept Person");
        dto.setEmail("dept@test.com");
        dto.setPassword("Strong1234!");
        dto.setAge(28);
        dto.setDepartmentId(dept.getId());

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department.name", is("Engineering")));
    }
}
