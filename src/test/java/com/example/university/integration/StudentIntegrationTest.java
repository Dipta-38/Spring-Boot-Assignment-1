package com.example.university.integration;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Role;
import com.example.university.entity.Student;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.DepartmentRepository;
import com.example.university.repository.StudentRepository;
import com.example.university.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String uniqueId;

    @BeforeEach
    void setUp() {
        // Delete in correct order to avoid FK constraints
        // First delete join tables
        studentRepository.deleteAll(); // This will handle student_courses via cascade?
        courseRepository.deleteAll();

        // Generate unique ID for this test run
        uniqueId = String.valueOf(System.currentTimeMillis());
    }

    @Test
    void createStudent_ShouldPersistToDatabase() {
        // Create student with unique data
        Student student = new Student();
        student.setUsername("student1-" + uniqueId);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setEmail("student1." + uniqueId + "@test.com");
        student.setStudentId("STU1-" + uniqueId);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);

        // Save
        Student saved = studentRepository.save(student);

        // Verify
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("student1-" + uniqueId);
        assertThat(saved.getStudentId()).isEqualTo("STU1-" + uniqueId);
    }

    @Test
    void getAllStudents_ShouldReturnAllPersistedStudents() {
        // Create first student
        Student student1 = new Student();
        student1.setUsername("student1-" + uniqueId);
        student1.setPassword(passwordEncoder.encode("password123"));
        student1.setEmail("student1." + uniqueId + "@test.com");
        student1.setStudentId("STU1-" + uniqueId);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setRole(Role.ROLE_STUDENT);
        student1.setEnabled(true);
        studentRepository.save(student1);

        // Create second student
        Student student2 = new Student();
        student2.setUsername("student2-" + uniqueId);
        student2.setPassword(passwordEncoder.encode("password123"));
        student2.setEmail("student2." + uniqueId + "@test.com");
        student2.setStudentId("STU2-" + uniqueId);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setRole(Role.ROLE_STUDENT);
        student2.setEnabled(true);
        studentRepository.save(student2);

        // Get all students
        List<Student> students = studentService.getAllStudents();

        // Verify exactly 2 students
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getUsername)
                .containsExactlyInAnyOrder("student1-" + uniqueId, "student2-" + uniqueId);
    }

    @Test
    void getStudentByUsername_ShouldReturnStudent() {
        // Create student
        Student student = new Student();
        student.setUsername("findbyname-" + uniqueId);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setEmail("find." + uniqueId + "@test.com");
        student.setStudentId("STU-FIND-" + uniqueId);
        student.setFirstName("Find");
        student.setLastName("Me");
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        studentRepository.save(student);

        // Find by username
        Student found = studentService.getStudentByUsername("findbyname-" + uniqueId);

        // Verify
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("findbyname-" + uniqueId);
        assertThat(found.getStudentId()).isEqualTo("STU-FIND-" + uniqueId);
    }

    @Test
    void updateStudent_ShouldModifyExistingStudent() {
        // Create student
        Student student = new Student();
        student.setUsername("update-" + uniqueId);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setEmail("update." + uniqueId + "@test.com");
        student.setStudentId("STU-UPDATE-" + uniqueId);
        student.setFirstName("Original");
        student.setLastName("Name");
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        Student saved = studentRepository.save(student);

        // Update
        saved.setFirstName("Updated");
        saved.setLastName("Changed");
        saved.setEmail("updated." + uniqueId + "@test.com");
        Student updated = studentService.updateStudent(saved);

        // Verify
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getLastName()).isEqualTo("Changed");
        assertThat(updated.getEmail()).isEqualTo("updated." + uniqueId + "@test.com");
    }

    @Test
    void deleteStudent_ShouldRemoveFromDatabase() {
        // Create student
        Student student = new Student();
        student.setUsername("delete-" + uniqueId);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setEmail("delete." + uniqueId + "@test.com");
        student.setStudentId("STU-DELETE-" + uniqueId);
        student.setFirstName("Delete");
        student.setLastName("Me");
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        Student saved = studentRepository.save(student);
        Long id = saved.getId();

        // Delete
        studentService.deleteStudent(id);

        // Verify
        assertThat(studentRepository.findById(id)).isEmpty();
    }

    @Test
    void enrollAndUnenroll_ShouldWork() {
        // Create department
        Department dept = new Department();
        dept.setName("CS Dept " + uniqueId);
        dept.setCode("CS-" + uniqueId);
        dept.setDescription("Test");
        dept = departmentRepository.save(dept);

        // Create course
        Course course = new Course();
        course.setName("Java " + uniqueId);
        course.setCode("JAVA-" + uniqueId);
        course.setCredits(3);
        course.setDepartment(dept);
        course = courseRepository.save(course);

        // Create student
        Student student = new Student();
        student.setUsername("enroll-" + uniqueId);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setEmail("enroll." + uniqueId + "@test.com");
        student.setStudentId("STU-ENROLL-" + uniqueId);
        student.setFirstName("Enroll");
        student.setLastName("Test");
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        Student saved = studentRepository.save(student);

        // Enroll
        studentService.enrollInCourse(saved.getId(), course.getId());

        // Check enrolled
        Student afterEnroll = studentRepository.findById(saved.getId()).get();
        assertThat(afterEnroll.getEnrolledCourses()).hasSize(1);
        assertThat(afterEnroll.getEnrolledCourses()).extracting(Course::getCode)
                .contains(course.getCode());

        // Unenroll
        studentService.unenrollFromCourse(saved.getId(), course.getId());

        // Check unenrolled
        Student afterUnenroll = studentRepository.findById(saved.getId()).get();
        assertThat(afterUnenroll.getEnrolledCourses()).isEmpty();
    }

    @Test
    void getAvailableCourses_ShouldReturnOnlyUnenrolledCourses() {
        // Create department
        Department dept = new Department();
        dept.setName("Test Dept " + uniqueId);
        dept.setCode("TEST-" + uniqueId);
        dept.setDescription("Test");
        dept = departmentRepository.save(dept);

        // Create course 1
        Course course1 = new Course();
        course1.setName("Course 1 " + uniqueId);
        course1.setCode("C1-" + uniqueId);
        course1.setCredits(3);
        course1.setDepartment(dept);
        course1 = courseRepository.save(course1);

        // Create course 2
        Course course2 = new Course();
        course2.setName("Course 2 " + uniqueId);
        course2.setCode("C2-" + uniqueId);
        course2.setCredits(3);
        course2.setDepartment(dept);
        course2 = courseRepository.save(course2);

        // Create student
        Student student = new Student();
        student.setUsername("available-" + uniqueId);
        student.setPassword(passwordEncoder.encode("password123"));
        student.setEmail("available." + uniqueId + "@test.com");
        student.setStudentId("STU-AVAIL-" + uniqueId);
        student.setFirstName("Available");
        student.setLastName("Test");
        student.setRole(Role.ROLE_STUDENT);
        student.setEnabled(true);
        Student saved = studentRepository.save(student);

        // Enroll in course 1 only
        studentService.enrollInCourse(saved.getId(), course1.getId());

        // Get available courses
        List<Course> available = studentService.getAvailableCourses(saved.getId());

        // Verify only course 2 is available
        assertThat(available).hasSize(1);
        assertThat(available.get(0).getCode()).isEqualTo("C2-" + uniqueId);
    }

    @Test
    void getStudentById_ShouldThrowException_WhenNotFound() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            studentService.getStudentById(99999L);
        });
        assertThat(exception.getMessage()).isEqualTo("Student not found");
    }
}