package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.*;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PasswordUtil passwordUtil;

    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    public Person addPerson(PersonCreateDTO personDTO) throws ValidationException {

        person.setName(personDTO.getName());
        person.setAge(personDTO.getAge());
        person.setEmail(personDTO.getEmail());
        String hashedPassword = passwordUtil.hashPassword(personDTO.getPassword());
        person.setPassword(hashedPassword);

        // 1. Email uniqueness
        if (personRepository.findByEmail(personDTO.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }

        // 2. Age must be >= 18
        if (personDTO.getAge() < 18) {
            throw new ValidationException("Person must be at least 18 years old");
        }

        // 3. Password must NOT contain name
        if (personDTO.getPassword().toLowerCase().contains(personDTO.getName().toLowerCase())) {
            throw new ValidationException("Password must not contain the name");
        }

        // 4. Department must exist
        Department department = null;
        if (personDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(personDTO.getDepartmentId())
                    .orElseThrow(() -> new ValidationException("Department not found"));
        }

        // 5. Max 5 projects
        List<Project> projects = null;
        if (personDTO.getProjectIds() != null) {
            if (personDTO.getProjectIds().size() > 5) {
                throw new ValidationException("A person cannot have more than 5 projects");
            }
            projects = projectRepository.findAllById(personDTO.getProjectIds());
        }

        // CREATE ENTITY
        Person person = new Person();
        person.setName(personDTO.getName());
        person.setAge(personDTO.getAge());
        person.setEmail(personDTO.getEmail());
        person.setPassword(personDTO.getPassword());
        person.setDepartment(department);
        person.setProjects(projects);

        return personRepository.save(person);
    }

    public Person updatePerson(UUID uuid, Person person) throws ValidationException {
        return personRepository.findById(uuid)
                .map(existingPerson -> {
                    existingPerson.setName(person.getName());
                    existingPerson.setAge(person.getAge());
                    existingPerson.setEmail(person.getEmail());
                    existingPerson.setPassword(person.getPassword());
                    existingPerson.setDepartment(person.getDepartment());
                    existingPerson.setProjects(person.getProjects());
                    return personRepository.save(existingPerson);
                }).orElseThrow(() -> new ValidationException("Person with id " + uuid + " not found"));
    }

    public void deletePerson(UUID uuid) {
        personRepository.deleteById(uuid);
    }

    public Person getPersonByEmail(String email) throws ValidationException {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Person with email " + email + " not found"));
    }

    public Person getPersonById(UUID uuid) throws ValidationException {
        return personRepository.findById(uuid)
                .orElseThrow(() -> new ValidationException("Person with id " + uuid + " not found"));
    }

    public Person patchPerson(UUID uuid, PersonPatchDTO dto) throws ValidationException {

        Person person = personRepository.findById(uuid)
                .orElseThrow(() -> new ValidationException("Person not found"));

        // update only if present

        if (dto.getName() != null) {
            person.setName(dto.getName());
        }

        if (dto.getAge() != null) {
            if (dto.getAge() < 18) {
                throw new ValidationException("Age must be at least 18");
            }
            person.setAge(dto.getAge());
        }

        if (dto.getEmail() != null) {
            // check uniqueness
            personRepository.findByEmail(dto.getEmail())
                    .filter(p -> !p.getId().equals(uuid))
                    .ifPresent(p -> {
                        throw new RuntimeException("Email already exists");
                    });

            person.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            if (dto.getPassword().toLowerCase().contains(person.getName().toLowerCase())) {
                throw new ValidationException("Password must not contain name");
            }
            person.setPassword(dto.getPassword());
        }

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ValidationException("Department not found"));
            person.setDepartment(dept);
        }

        if (dto.getProjectIds() != null) {
            if (dto.getProjectIds().size() > 5) {
                throw new ValidationException("Max 5 projects allowed");
            }
            List<Project> projects = projectRepository.findAllById(dto.getProjectIds());
            person.setProjects(projects);
        }

        return personRepository.save(person);
    }

}