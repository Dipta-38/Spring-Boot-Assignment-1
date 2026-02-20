package com.example.university.repository;

import com.example.university.entity.Role;
import com.example.university.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testFindByStudentId() {
        String uniqueStudentId = "STU" + System.currentTimeMillis();
        String uniqueUsername = "student" + System.currentTimeMillis();

        Student student = new Student();
        student.setUsername(uniqueUsername);
        student.setEmail(uniqueUsername + "@test.com");
        student.setPassword("password");
        student.setRole(Role.ROLE_STUDENT);
        student.setStudentId(uniqueStudentId);
        student.setFirstName("Test");
        student.setLastName("Student");
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByStudentId(uniqueStudentId);
        assertThat(found).isPresent();
        assertThat(found.get().getStudentId()).isEqualTo(uniqueStudentId);
    }

    @Test
    void testExistsByStudentId() {
        String uniqueStudentId = "STU" + System.currentTimeMillis();
        String uniqueUsername = "student" + System.currentTimeMillis();

        Student student = new Student();
        student.setUsername(uniqueUsername);
        student.setEmail(uniqueUsername + "@test.com");
        student.setPassword("password");
        student.setRole(Role.ROLE_STUDENT);
        student.setStudentId(uniqueStudentId);
        student.setFirstName("Test");
        student.setLastName("Student");
        studentRepository.save(student);

        boolean exists = studentRepository.existsByStudentId(uniqueStudentId);
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByUsername() {
        String uniqueUsername = "student" + System.currentTimeMillis();
        String uniqueStudentId = "STU" + System.currentTimeMillis();

        Student student = new Student();
        student.setUsername(uniqueUsername);
        student.setEmail(uniqueUsername + "@test.com");
        student.setPassword("password");
        student.setRole(Role.ROLE_STUDENT);
        student.setStudentId(uniqueStudentId);
        student.setFirstName("Test");
        student.setLastName("Student");
        studentRepository.save(student);

        Optional<Student> found = studentRepository.findByUsername(uniqueUsername);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(uniqueUsername);
    }
}