package com.course.leverxproject.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name="roles")
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="role_name")
    private String name;


}
