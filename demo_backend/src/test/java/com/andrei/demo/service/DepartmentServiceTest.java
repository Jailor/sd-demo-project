package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Department;
import com.andrei.demo.model.DepartmentCreateDTO;
import com.andrei.demo.repository.DepartmentRepository;
import com.andrei.demo.repository.PersonRepository;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(UUID.randomUUID());
        department.setName("Engineering");
    }

    @Test
    void getDepartments_returnsAll() {
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        List<Department> result = departmentService.getDepartments();

        assertEquals(1, result.size());
        assertEquals("Engineering", result.get(0).getName());
    }

    @Test
    void getDepartments_returnsEmptyList() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Department> result = departmentService.getDepartments();

        assertTrue(result.isEmpty());
    }

    @Test
    void getDepartmentById_existing_returnsDepartment() {
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentById(department.getId());

        assertEquals("Engineering", result.getName());
    }

    @Test
    void getDepartmentById_nonExistent_throwsException() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> departmentService.getDepartmentById(id));
    }

    @Test
    void addDepartment_savesAndReturns() {
        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setName("Marketing");

        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        Department result = departmentService.addDepartment(dto);

        assertNotNull(result.getId());
        assertEquals("Marketing", result.getName());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void updateDepartment_existing_updatesAndReturns() {
        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setName("Updated Name");

        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

        Department result = departmentService.updateDepartment(department.getId(), dto);

        assertEquals("Updated Name", result.getName());
    }

    @Test
    void updateDepartment_nonExistent_throwsException() {
        UUID id = UUID.randomUUID();
        DepartmentCreateDTO dto = new DepartmentCreateDTO();
        dto.setName("Whatever");
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> departmentService.updateDepartment(id, dto));
    }

    @Test
    void deleteDepartment_noPersons_callsRepository() {
        UUID id = UUID.randomUUID();
        when(personRepository.existsByDepartmentId(id)).thenReturn(false);
        departmentService.deleteDepartment(id);
        verify(departmentRepository).deleteById(id);
    }

    @Test
    void deleteDepartment_withPersons_throwsValidationException() {
        UUID id = UUID.randomUUID();
        when(personRepository.existsByDepartmentId(id)).thenReturn(true);
        assertThrows(ValidationException.class, () -> departmentService.deleteDepartment(id));
        verify(departmentRepository, never()).deleteById(any());
    }
}
