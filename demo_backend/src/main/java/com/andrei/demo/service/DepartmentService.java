package com.andrei.demo.service;

import com.andrei.demo.model.Department;
import com.andrei.demo.model.DepartmentCreateDTO;
import com.andrei.demo.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Department not found"));
    }

    public Department addDepartment(DepartmentCreateDTO dto) {
        Department department = new Department();
        department.setName(dto.getName());
        return departmentRepository.save(department);
    }

    public Department updateDepartment(UUID id, DepartmentCreateDTO dto) {
        Department department = getDepartmentById(id);
        department.setName(dto.getName());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(UUID id) {
        departmentRepository.deleteById(id);
    }
}