package com.example.university.repository;

import com.example.university.entity.Role;
import com.example.university.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void testFindByTeacherId() {
        String uniqueTeacherId = "TCH" + System.currentTimeMillis();
        String uniqueUsername = "teacher" + System.currentTimeMillis();

        Teacher teacher = new Teacher();
        teacher.setUsername(uniqueUsername);
        teacher.setEmail(uniqueUsername + "@test.com");
        teacher.setPassword("password");
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setTeacherId(uniqueTeacherId);
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setQualification("PhD");
        teacherRepository.save(teacher);

        Optional<Teacher> found = teacherRepository.findByTeacherId(uniqueTeacherId);
        assertThat(found).isPresent();
        assertThat(found.get().getTeacherId()).isEqualTo(uniqueTeacherId);
    }

    @Test
    void testExistsByTeacherId() {
        String uniqueTeacherId = "TCH" + System.currentTimeMillis();
        String uniqueUsername = "teacher" + System.currentTimeMillis();

        Teacher teacher = new Teacher();
        teacher.setUsername(uniqueUsername);
        teacher.setEmail(uniqueUsername + "@test.com");
        teacher.setPassword("password");
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setTeacherId(uniqueTeacherId);
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setQualification("PhD");
        teacherRepository.save(teacher);

        boolean exists = teacherRepository.existsByTeacherId(uniqueTeacherId);
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByUsername() {
        String uniqueUsername = "teacher" + System.currentTimeMillis();
        String uniqueTeacherId = "TCH" + System.currentTimeMillis();

        Teacher teacher = new Teacher();
        teacher.setUsername(uniqueUsername);
        teacher.setEmail(uniqueUsername + "@test.com");
        teacher.setPassword("password");
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setTeacherId(uniqueTeacherId);
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setQualification("PhD");
        teacherRepository.save(teacher);

        Optional<Teacher> found = teacherRepository.findByUsername(uniqueUsername);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(uniqueUsername);
    }
}