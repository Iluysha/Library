package com.epam.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name="roles")
public class Role {

    //Unique id for each role
    @Id
    private Integer id;

    //The role
    @Column(nullable = false, unique = true)
    private String role;

}
