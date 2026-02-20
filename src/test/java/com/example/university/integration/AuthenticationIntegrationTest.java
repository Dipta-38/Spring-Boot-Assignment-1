package com.example.university.integration;

import com.example.university.entity.Role;
import com.example.university.entity.Student;
import com.example.university.entity.Teacher;
import com.example.university.entity.User;
import com.example.university.repository.StudentRepository;
import com.example.university.repository.TeacherRepository;
import com.example.university.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AuthenticationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerStudent_ShouldSaveToDatabase() {
        // Create student
        Student student = new Student();
        student.setUsername("john_student");
        student.setEmail("john.student@test.com");
        student.setPassword(passwordEncoder.encode("password123"));
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        student.setStudentId("STU123");
        student.setFirstName("John");
        student.setLastName("Doe");

        // Save
        Student saved = studentRepository.save(student);

        // Verify
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("john_student");
        assertThat(saved.getEmail()).isEqualTo("john.student@test.com");
        assertThat(saved.getStudentId()).isEqualTo("STU123");
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getRole()).isEqualTo(Role.ROLE_STUDENT);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void registerTeacher_ShouldSaveToDatabase() {
        // Create teacher
        Teacher teacher = new Teacher();
        teacher.setUsername("jane_teacher");
        teacher.setEmail("jane.teacher@test.com");
        teacher.setPassword(passwordEncoder.encode("password123"));
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setEnabled(true);
        teacher.setTeacherId("TCH456");
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setQualification("PhD in Computer Science");

        // Save
        Teacher saved = teacherRepository.save(teacher);

        // Verify
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("jane_teacher");
        assertThat(saved.getEmail()).isEqualTo("jane.teacher@test.com");
        assertThat(saved.getTeacherId()).isEqualTo("TCH456");
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getLastName()).isEqualTo("Smith");
        assertThat(saved.getQualification()).isEqualTo("PhD in Computer Science");
        assertThat(saved.getRole()).isEqualTo(Role.ROLE_TEACHER);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void registerAdmin_ShouldSaveToDatabase() {
        // Create admin
        User admin = new User();
        admin.setUsername("admin_user");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEnabled(true);

        // Save
        User saved = userRepository.save(admin);

        // Verify
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("admin_user");
        assertThat(saved.getEmail()).isEqualTo("admin@test.com");
        assertThat(saved.getRole()).isEqualTo(Role.ROLE_ADMIN);
        assertThat(saved.isEnabled()).isTrue();
    }

    @Test
    void findStudentByUsername_ShouldReturnStudent() {
        // Create student
        Student student = new Student();
        student.setUsername("findstudent");
        student.setEmail("find@test.com");
        student.setPassword(passwordEncoder.encode("password"));
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        student.setStudentId("STU999");
        student.setFirstName("Find");
        student.setLastName("Me");
        studentRepository.save(student);

        // Find by username
        User found = userRepository.findByUsername("findstudent").orElse(null);

        // Verify
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("findstudent");
        assertThat(found.getEmail()).isEqualTo("find@test.com");
        assertThat(found.getRole()).isEqualTo(Role.ROLE_STUDENT);
    }

    @Test
    void findTeacherByEmail_ShouldReturnTeacher() {
        // Create teacher
        Teacher teacher = new Teacher();
        teacher.setUsername("findteacher");
        teacher.setEmail("teacher.find@test.com");
        teacher.setPassword(passwordEncoder.encode("password"));
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setEnabled(true);
        teacher.setTeacherId("TCH888");
        teacher.setFirstName("Find");
        teacher.setLastName("Teacher");
        teacherRepository.save(teacher);

        // Find by email
        User found = userRepository.findByEmail("teacher.find@test.com").orElse(null);

        // Verify
        assertThat(found).isNotNull();
        assertThat(found).isInstanceOf(Teacher.class);
        assertThat(found.getUsername()).isEqualTo("findteacher");
        assertThat(found.getEmail()).isEqualTo("teacher.find@test.com");
        assertThat(found.getRole()).isEqualTo(Role.ROLE_TEACHER);

        Teacher foundTeacher = (Teacher) found;
        assertThat(foundTeacher.getTeacherId()).isEqualTo("TCH888");
    }

    @Test
    void existsByUsername_ShouldReturnTrue() {
        // Create user
        User user = new User();
        user.setUsername("checkuser");
        user.setEmail("check@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        // Check
        boolean exists = userRepository.existsByUsername("checkuser");

        // Verify
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnTrue() {
        // Create user
        User user = new User();
        user.setUsername("checkemail");
        user.setEmail("email.check@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.ROLE_TEACHER);
        user.setEnabled(true);
        userRepository.save(user);

        // Check
        boolean exists = userRepository.existsByEmail("email.check@test.com");

        // Verify
        assertThat(exists).isTrue();
    }

    @Test
    void existsByStudentId_ShouldReturnTrue() {
        // Create student
        Student student = new Student();
        student.setUsername("studentid");
        student.setEmail("student.id@test.com");
        student.setPassword(passwordEncoder.encode("password"));
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        student.setStudentId("STU4567");
        student.setFirstName("ID");
        student.setLastName("Test");
        studentRepository.save(student);

        // Check
        boolean exists = studentRepository.existsByStudentId("STU4567");

        // Verify
        assertThat(exists).isTrue();
    }

    @Test
    void existsByTeacherId_ShouldReturnTrue() {
        // Create teacher
        Teacher teacher = new Teacher();
        teacher.setUsername("teacherid");
        teacher.setEmail("teacher.id@test.com");
        teacher.setPassword(passwordEncoder.encode("password"));
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setEnabled(true);
        teacher.setTeacherId("TCH7890");
        teacher.setFirstName("ID");
        teacher.setLastName("Test");
        teacherRepository.save(teacher);

        // Check
        boolean exists = teacherRepository.existsByTeacherId("TCH7890");

        // Verify
        assertThat(exists).isTrue();
    }

    @Test
    void password_ShouldBeEncoded() {
        // Create user with raw password
        User user = new User();
        user.setUsername("passwordtest");
        user.setEmail("password@test.com");
        user.setPassword(passwordEncoder.encode("rawPassword123"));
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        // Fetch and verify
        User found = userRepository.findByUsername("passwordtest").orElse(null);
        assertThat(found).isNotNull();

        // Password should be encoded (not equal to raw)
        assertThat(found.getPassword()).isNotEqualTo("rawPassword123");
        // But should match with encoder
        assertThat(passwordEncoder.matches("rawPassword123", found.getPassword())).isTrue();
    }
}