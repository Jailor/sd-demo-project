package com.andrei.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Entity
@Data
@ToString(exclude = "person")
@EqualsAndHashCode(exclude = "person")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String description;

    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Person person;
}