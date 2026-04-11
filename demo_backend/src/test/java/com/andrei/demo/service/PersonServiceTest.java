package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.*;
import com.andrei.demo.repository.DepartmentRepository;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private PersonService personService;

    private Person existingPerson;
    private Department department;
    private Project project;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("Engineering");

        project = new Project();
        project.setId(UUID.randomUUID());
        project.setTitle("Project Alpha");

        existingPerson = new Person();
        existingPerson.setId(UUID.randomUUID());
        existingPerson.setName("John Doe");
        existingPerson.setEmail("john@test.com");
        existingPerson.setPassword("Secure1234!");
        existingPerson.setAge(25);
        existingPerson.setRole("USER");
        existingPerson.setDepartment(department);
        existingPerson.setProjects(List.of(project));
    }

    // ──────────── getPeople ────────────

    @Test
    void getPeople_returnsAllPersons() {
        when(personRepository.findAll()).thenReturn(List.of(existingPerson));

        List<Person> result = personService.getPeople();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(personRepository).findAll();
    }

    @Test
    void getPeople_returnsEmptyList() {
        when(personRepository.findAll()).thenReturn(Collections.emptyList());

        List<Person> result = personService.getPeople();

        assertTrue(result.isEmpty());
    }

    // ──────────── getPersonById ────────────

    @Test
    void getPersonById_existingId_returnsPerson() {
        when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));

        Person result = personService.getPersonById(existingPerson.getId());

        assertEquals(existingPerson.getName(), result.getName());
    }

    @Test
    void getPersonById_nonExistentId_throwsValidationException() {
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.getPersonById(id));
    }

    // ──────────── getPersonByEmail ────────────

    @Test
    void getPersonByEmail_existingEmail_returnsPerson() {
        when(personRepository.findByEmail("john@test.com")).thenReturn(Optional.of(existingPerson));

        Person result = personService.getPersonByEmail("john@test.com");

        assertEquals("John Doe", result.getName());
    }

    @Test
    void getPersonByEmail_nonExistentEmail_throwsValidationException() {
        when(personRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.getPersonByEmail("nobody@test.com"));
    }

    // ──────────── addPerson ────────────

    @Nested
    class AddPersonTests {

        private PersonCreateDTO validDto;

        @BeforeEach
        void setUp() {
            validDto = new PersonCreateDTO();
            validDto.setName("Jane Smith");
            validDto.setEmail("jane@test.com");
            validDto.setPassword("Secure1234!");
            validDto.setAge(22);
        }

        @Test
        void addPerson_withValidData_returnsSavedPerson() {
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> {
                Person p = inv.getArgument(0);
                p.setId(UUID.randomUUID());
                return p;
            });

            Person result = personService.addPerson(validDto);

            assertNotNull(result.getId());
            assertEquals("Jane Smith", result.getName());
            assertEquals("USER", result.getRole());
            verify(personRepository).save(any(Person.class));
        }

        @Test
        void addPerson_withDuplicateEmail_throwsValidationException() {
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.of(existingPerson));

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> personService.addPerson(validDto));
            assertEquals("Email already exists", ex.getMessage());
        }

        @Test
        void addPerson_withAgeLessThan18_throwsValidationException() {
            validDto.setAge(17);
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> personService.addPerson(validDto));
            assertEquals("Person must be at least 18 years old", ex.getMessage());
        }

        @Test
        void addPerson_withPasswordContainingName_throwsValidationException() {
            validDto.setPassword("Jane Smith123!");
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> personService.addPerson(validDto));
            assertTrue(ex.getMessage().contains("Password must not contain the name"));
        }

        @Test
        void addPerson_withNonExistentDepartment_throwsValidationException() {
            UUID deptId = UUID.randomUUID();
            validDto.setDepartmentId(deptId);
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
            when(departmentRepository.findById(deptId)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class, () -> personService.addPerson(validDto));
        }

        @Test
        void addPerson_withValidDepartment_setsDepartment() {
            validDto.setDepartmentId(department.getId());
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
            when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.addPerson(validDto);

            assertEquals(department, result.getDepartment());
        }

        @Test
        void addPerson_withMoreThan5Projects_throwsValidationException() {
            List<UUID> projectIds = new ArrayList<>();
            for (int i = 0; i < 6; i++) projectIds.add(UUID.randomUUID());
            validDto.setProjectIds(projectIds);
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());

            ValidationException ex = assertThrows(ValidationException.class,
                    () -> personService.addPerson(validDto));
            assertTrue(ex.getMessage().contains("5 projects"));
        }

        @Test
        void addPerson_with5Projects_succeeds() {
            List<UUID> projectIds = new ArrayList<>();
            List<Project> projects = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                UUID pid = UUID.randomUUID();
                Project p = new Project();
                p.setId(pid);
                p.setTitle("Project " + i);
                projectIds.add(pid);
                projects.add(p);
            }
            validDto.setProjectIds(projectIds);
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
            when(projectRepository.findAllById(projectIds)).thenReturn(projects);
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.addPerson(validDto);

            assertEquals(5, result.getProjects().size());
        }

        @Test
        void addPerson_withAdminRole_setsAdminRole() {
            validDto.setRole("ADMIN");
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.addPerson(validDto);

            assertEquals("ADMIN", result.getRole());
        }

        @Test
        void addPerson_withNullRole_defaultsToUser() {
            validDto.setRole(null);
            when(personRepository.findByEmail("jane@test.com")).thenReturn(Optional.empty());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.addPerson(validDto);

            assertEquals("USER", result.getRole());
        }
    }

    // ──────────── updatePerson ────────────

    @Test
    void updatePerson_existingId_updatesAndReturns() {
        Person updated = new Person();
        updated.setName("Updated Name");
        updated.setAge(30);
        updated.setEmail("updated@test.com");
        updated.setPassword("NewPass1234!");
        updated.setDepartment(department);
        updated.setProjects(List.of(project));

        when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

        Person result = personService.updatePerson(existingPerson.getId(), updated);

        assertEquals("Updated Name", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("updated@test.com", result.getEmail());
    }

    @Test
    void updatePerson_nonExistentId_throwsValidationException() {
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.updatePerson(id, new Person()));
    }

    // ──────────── deletePerson ────────────

    @Test
    void deletePerson_callsRepository() {
        UUID id = UUID.randomUUID();

        personService.deletePerson(id);

        verify(personRepository).deleteById(id);
    }

    // ──────────── patchPerson ────────────

    @Nested
    class PatchPersonTests {

        @Test
        void patchPerson_updateName_onlyNameChanges() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setName("New Name");

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.patchPerson(existingPerson.getId(), dto);

            assertEquals("New Name", result.getName());
            assertEquals(25, result.getAge()); // unchanged
        }

        @Test
        void patchPerson_updateAge_validAge_succeeds() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setAge(35);

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.patchPerson(existingPerson.getId(), dto);

            assertEquals(35, result.getAge());
        }

        @Test
        void patchPerson_updateAge_underAge_throwsValidationException() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setAge(16);

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));

            assertThrows(ValidationException.class,
                    () -> personService.patchPerson(existingPerson.getId(), dto));
        }

        @Test
        void patchPerson_updateEmail_uniqueEmail_succeeds() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setEmail("newemail@test.com");

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(personRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.patchPerson(existingPerson.getId(), dto);

            assertEquals("newemail@test.com", result.getEmail());
        }

        @Test
        void patchPerson_updateEmail_duplicateEmail_throwsException() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setEmail("taken@test.com");

            Person otherPerson = new Person();
            otherPerson.setId(UUID.randomUUID());
            otherPerson.setEmail("taken@test.com");

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(personRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(otherPerson));

            assertThrows(RuntimeException.class,
                    () -> personService.patchPerson(existingPerson.getId(), dto));
        }

        @Test
        void patchPerson_updatePassword_containsName_throwsValidationException() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setPassword("John Doe123!");

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));

            assertThrows(ValidationException.class,
                    () -> personService.patchPerson(existingPerson.getId(), dto));
        }

        @Test
        void patchPerson_updatePassword_valid_succeeds() {
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setPassword("ValidPass99!");

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.patchPerson(existingPerson.getId(), dto);

            assertEquals("ValidPass99!", result.getPassword());
        }

        @Test
        void patchPerson_updateDepartment_existingDept_succeeds() {
            Department newDept = new Department();
            newDept.setId(UUID.randomUUID());
            newDept.setName("Marketing");

            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setDepartmentId(newDept.getId());

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(departmentRepository.findById(newDept.getId())).thenReturn(Optional.of(newDept));
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            Person result = personService.patchPerson(existingPerson.getId(), dto);

            assertEquals("Marketing", result.getDepartment().getName());
        }

        @Test
        void patchPerson_updateDepartment_nonExistentDept_throwsValidationException() {
            UUID deptId = UUID.randomUUID();
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setDepartmentId(deptId);

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));
            when(departmentRepository.findById(deptId)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> personService.patchPerson(existingPerson.getId(), dto));
        }

        @Test
        void patchPerson_updateProjects_moreThan5_throwsValidationException() {
            List<UUID> projectIds = new ArrayList<>();
            for (int i = 0; i < 6; i++) projectIds.add(UUID.randomUUID());
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setProjectIds(projectIds);

            when(personRepository.findById(existingPerson.getId())).thenReturn(Optional.of(existingPerson));

            assertThrows(ValidationException.class,
                    () -> personService.patchPerson(existingPerson.getId(), dto));
        }

        @Test
        void patchPerson_nonExistentPerson_throwsValidationException() {
            UUID id = UUID.randomUUID();
            PersonPatchDTO dto = new PersonPatchDTO();
            dto.setName("Whatever");

            when(personRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> personService.patchPerson(id, dto));
        }
    }
}
