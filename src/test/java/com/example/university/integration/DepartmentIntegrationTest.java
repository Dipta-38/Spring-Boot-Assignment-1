package com.example.university.integration;

import com.example.university.entity.Department;
import com.example.university.repository.DepartmentRepository;
import com.example.university.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DepartmentIntegrationTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    private String uniqueId;
    private Department department;

    @BeforeEach
    void setUp() {
        uniqueId = String.valueOf(System.currentTimeMillis());

        department = new Department();
        department.setName("Computer Science " + uniqueId);
        department.setCode("CS-" + uniqueId.substring(uniqueId.length() - 4));
        department.setDescription("Computer Science Department");
    }

    @Test
    void createDepartment_ShouldPersistToDatabase() {
        Department saved = departmentService.createDepartment(department);

        Department found = departmentRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(department.getName());
        assertThat(found.getCode()).isEqualTo(department.getCode());
    }

    @Test
    void createDepartment_WithDuplicateName_ShouldThrowException() {
        departmentService.createDepartment(department);

        Department duplicate = new Department();
        duplicate.setName(department.getName()); // Same name
        duplicate.setCode("MATH-" + uniqueId.substring(uniqueId.length() - 4));

        assertThatThrownBy(() -> departmentService.createDepartment(duplicate))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department name already exists");
    }

    @Test
    void updateDepartment_ShouldModifyExisting() {
        Department saved = departmentService.createDepartment(department);

        saved.setName("Updated Name " + uniqueId);
        saved.setCode("UPD-" + uniqueId.substring(uniqueId.length() - 4));
        Department updated = departmentService.updateDepartment(saved.getId(), saved);

        assertThat(updated.getName()).isEqualTo("Updated Name " + uniqueId);
        assertThat(updated.getCode()).isEqualTo("UPD-" + uniqueId.substring(uniqueId.length() - 4));
    }
}