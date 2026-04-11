package com.andrei.demo.controller;

import com.andrei.demo.model.LoginRequest;
import com.andrei.demo.model.LoginResponse;
import com.andrei.demo.model.Person;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();

        Person admin = new Person();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword("Admin1234!");
        admin.setAge(30);
        admin.setRole("ADMIN");
        personRepository.save(admin);

        Person user = new Person();
        user.setName("User");
        user.setEmail("user@test.com");
        user.setPassword("User1234!");
        user.setAge(25);
        user.setRole("USER");
        personRepository.save(user);
    }

    @Test
    void login_withValidAdminCredentials_returnsOkWithAdminRole() throws Exception {
        LoginRequest request = new LoginRequest("admin@test.com", "Admin1234!");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);

        assertTrue(response.success());
        assertEquals("ADMIN", response.role());
        assertNull(response.errorMessage());
    }

    @Test
    void login_withValidUserCredentials_returnsOkWithUserRole() throws Exception {
        LoginRequest request = new LoginRequest("user@test.com", "User1234!");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);

        assertTrue(response.success());
        assertEquals("USER", response.role());
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("admin@test.com", "WrongPassword");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);

        assertFalse(response.success());
        assertEquals("Incorrect password", response.errorMessage());
    }

    @Test
    void login_withNonExistentEmail_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("nobody@test.com", "Pass1234!");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);

        assertFalse(response.success());
        assertTrue(response.errorMessage().contains("not found"));
    }

    @Test
    void login_withEmptyBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
