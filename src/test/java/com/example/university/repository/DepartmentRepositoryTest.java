package com.example.university.repository;

import com.example.university.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void testExistsByName() {
        // Use a unique name with timestamp to avoid conflicts
        String uniqueName = "Test Department " + System.currentTimeMillis();

        Department dept = new Department();
        dept.setName(uniqueName);
        dept.setCode("TEST" + System.currentTimeMillis());
        dept.setDescription("Test Department");
        departmentRepository.save(dept);

        boolean exists = departmentRepository.existsByName(uniqueName);
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCode() {
        String uniqueCode = "TEST" + System.currentTimeMillis();

        Department dept = new Department();
        dept.setName("Test Dept " + System.currentTimeMillis());
        dept.setCode(uniqueCode);
        dept.setDescription("Test Department");
        departmentRepository.save(dept);

        boolean exists = departmentRepository.existsByCode(uniqueCode);
        assertThat(exists).isTrue();
    }
}