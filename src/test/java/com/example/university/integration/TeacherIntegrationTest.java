package com.example.university.integration;

import com.example.university.entity.Role;
import com.example.university.entity.Teacher;
import com.example.university.repository.TeacherRepository;
import com.example.university.service.TeacherService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeacherIntegrationTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testTeacher = new Teacher();
        testTeacher.setUsername("teacher1");
        testTeacher.setPassword(passwordEncoder.encode("password123"));
        testTeacher.setEmail("teacher@test.com");
        testTeacher.setTeacherId("TEACH001");
        testTeacher.setFirstName("John");
        testTeacher.setLastName("Doe");
        testTeacher.setQualification("PhD");
        testTeacher.setRole(Role.ROLE_TEACHER);
        testTeacher.setEnabled(true);
    }

    @AfterEach
    void tearDown() {
        teacherRepository.deleteAll();
    }

    @Test
    void createTeacher_ShouldPersistToDatabase() {
        Teacher saved = teacherRepository.save(testTeacher);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());

        Teacher found = teacherRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
    }

    @Test
    void updateTeacher_ShouldModifyExisting() {
        Teacher saved = teacherRepository.save(testTeacher);

        saved.setFirstName("Jane");
        saved.setQualification("Masters");

        Teacher updated = teacherService.updateTeacher(saved);

        assertEquals("Jane", updated.getFirstName());
        assertEquals("Masters", updated.getQualification());
    }

    @Test
    void deleteTeacher_ShouldRemoveFromDatabase() {
        Teacher saved = teacherRepository.save(testTeacher);

        teacherService.deleteTeacher(saved.getId());

        assertFalse(teacherRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void getTeacherByUsername_ShouldReturnTeacher() {
        teacherRepository.save(testTeacher);

        Teacher found = teacherService.getTeacherByUsername("teacher1");

        assertNotNull(found);
        assertEquals("teacher1", found.getUsername());
    }
}