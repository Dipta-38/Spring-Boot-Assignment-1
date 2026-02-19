package com.example.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Department code is required")
    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<Teacher> teachers = new HashSet<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

    @ManyToMany(mappedBy = "departments", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();

    public Department() {}

    public Department(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Teacher> getTeachers() { return teachers; }
    public void setTeachers(Set<Teacher> teachers) { this.teachers = teachers; }

    public Set<Course> getCourses() { return courses; }
    public void setCourses(Set<Course> courses) { this.courses = courses; }

    public Set<Student> getStudents() { return students; }
    public void setStudents(Set<Student> students) { this.students = students; }
}