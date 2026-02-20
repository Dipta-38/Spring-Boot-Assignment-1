package com.example.university.integration;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Teacher;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.DepartmentRepository;
import com.example.university.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CourseIntegrationTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Department department;
    private Teacher teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        // Use unique names with timestamp
        String uniqueId = String.valueOf(System.currentTimeMillis());

        department = new Department();
        department.setName("Computer Science " + uniqueId);
        department.setCode("CS-" + uniqueId.substring(uniqueId.length() - 4));
        department.setDescription("Computer Science Department");
        departmentRepository.save(department);

        teacher = new Teacher();
        teacher.setUsername("teacher." + uniqueId);
        teacher.setEmail("teacher." + uniqueId + "@university.com");
        teacher.setPassword("password");
        teacher.setRole(com.example.university.entity.Role.ROLE_TEACHER);
        teacher.setTeacherId("TCH-" + uniqueId.substring(uniqueId.length() - 4));
        teacher.setFirstName("John");
        teacher.setLastName("Smith");
        teacher.setDepartment(department);
        teacherRepository.save(teacher);

        course = new Course();
        course.setName("Java Programming " + uniqueId);
        course.setCode("CS101-" + uniqueId.substring(uniqueId.length() - 4));
        course.setCredits(3);
        course.setDepartment(department);
        course.setTeacher(teacher);
        courseRepository.save(course);
    }

    @Test
    void createCourse_ShouldPersistToDatabase() {
        Course found = courseRepository.findById(course.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(course.getName());
        assertThat(found.getCode()).isEqualTo(course.getCode());
        assertThat(found.getDepartment().getName()).isEqualTo(department.getName());
        assertThat(found.getTeacher().getTeacherId()).isEqualTo(teacher.getTeacherId());
    }

    @Test
    void deleteCourse_ShouldRemoveFromDatabase() {
        courseRepository.delete(course);
        Course found = courseRepository.findById(course.getId()).orElse(null);
        assertThat(found).isNull();
    }
}