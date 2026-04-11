package com.andrei.demo.service;

import com.andrei.demo.model.LoginResponse;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private SecurityService securityService;

    private Person adminPerson;
    private Person userPerson;

    @BeforeEach
    void setUp() {
        adminPerson = new Person();
        adminPerson.setId(UUID.randomUUID());
        adminPerson.setName("Admin User");
        adminPerson.setEmail("admin@test.com");
        adminPerson.setPassword("Admin1234!");
        adminPerson.setAge(30);
        adminPerson.setRole("ADMIN");

        userPerson = new Person();
        userPerson.setId(UUID.randomUUID());
        userPerson.setName("Regular User");
        userPerson.setEmail("user@test.com");
        userPerson.setPassword("User1234!");
        userPerson.setAge(25);
        userPerson.setRole("USER");
    }

    @Test
    void login_withValidAdminCredentials_returnsSuccessWithAdminRole() {
        when(personRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminPerson));

        LoginResponse response = securityService.login("admin@test.com", "Admin1234!");

        assertTrue(response.success());
        assertEquals("ADMIN", response.role());
        assertNull(response.errorMessage());
    }

    @Test
    void login_withValidUserCredentials_returnsSuccessWithUserRole() {
        when(personRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userPerson));

        LoginResponse response = securityService.login("user@test.com", "User1234!");

        assertTrue(response.success());
        assertEquals("USER", response.role());
        assertNull(response.errorMessage());
    }

    @Test
    void login_withInvalidPassword_returnsFailure() {
        when(personRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminPerson));

        LoginResponse response = securityService.login("admin@test.com", "WrongPassword");

        assertFalse(response.success());
        assertNull(response.role());
        assertEquals("Incorrect password", response.errorMessage());
    }

    @Test
    void login_withNonExistentEmail_returnsFailure() {
        when(personRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        LoginResponse response = securityService.login("nonexistent@test.com", "Any123!");

        assertFalse(response.success());
        assertNull(response.role());
        assertTrue(response.errorMessage().contains("not found"));
    }

    @Test
    void login_withEmptyEmail_returnsFailure() {
        when(personRepository.findByEmail("")).thenReturn(Optional.empty());

        LoginResponse response = securityService.login("", "password");

        assertFalse(response.success());
        assertNull(response.role());
    }

    @Test
    void login_verifyRepositoryCalledWithCorrectEmail() {
        when(personRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminPerson));

        securityService.login("admin@test.com", "Admin1234!");

        verify(personRepository, times(1)).findByEmail("admin@test.com");
    }
}
