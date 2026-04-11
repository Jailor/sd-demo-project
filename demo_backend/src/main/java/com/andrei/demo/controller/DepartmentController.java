package com.andrei.demo.controller;

import com.andrei.demo.model.Department;
import com.andrei.demo.model.DepartmentCreateDTO;
import com.andrei.demo.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public List<Department> getDepartments() {
        return departmentService.getDepartments();
    }

    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable UUID id) {
        return departmentService.getDepartmentById(id);
    }

    @PostMapping
    public Department addDepartment(@Valid @RequestBody DepartmentCreateDTO dto) {
        return departmentService.addDepartment(dto);
    }

    @PutMapping("/{id}")
    public Department updateDepartment(@PathVariable UUID id, @Valid @RequestBody DepartmentCreateDTO dto) {
        return departmentService.updateDepartment(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
    }
}