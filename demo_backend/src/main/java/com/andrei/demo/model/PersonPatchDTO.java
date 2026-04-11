package com.andrei.demo.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PersonPatchDTO {

    private String name;
    private String password;
    private Integer age;
    private String email;

    private UUID departmentId;
    private List<UUID> projectIds;
}