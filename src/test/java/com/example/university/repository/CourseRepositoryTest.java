package com.example.university.repository;

import com.example.university.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testExistsByName() {
        // Create department with unique name
        Department dept = createUniqueDepartment();

        // Create course with unique name
        String uniqueCourseName = "Course " + System.currentTimeMillis();
        Course course = new Course();
        course.setName(uniqueCourseName);
        course.setCode("C" + System.currentTimeMillis());
        course.setCredits(3);
        course.setDepartment(dept);
        courseRepository.save(course);

        boolean exists = courseRepository.existsByName(uniqueCourseName);
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCode() {
        Department dept = createUniqueDepartment();

        String uniqueCode = "C" + System.currentTimeMillis();
        Course course = new Course();
        course.setName("Test Course " + System.currentTimeMillis());
        course.setCode(uniqueCode);
        course.setCredits(3);
        course.setDepartment(dept);
        courseRepository.save(course);

        boolean exists = courseRepository.existsByCode(uniqueCode);
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByDepartmentId() {
        Department dept = createUniqueDepartment();

        Course course = new Course();
        course.setName("Course " + System.currentTimeMillis());
        course.setCode("C" + System.currentTimeMillis());
        course.setCredits(3);
        course.setDepartment(dept);
        courseRepository.save(course);

        List<Course> courses = courseRepository.findByDepartmentId(dept.getId());
        assertThat(courses).isNotEmpty();
    }

    private Department createUniqueDepartment() {
        Department dept = new Department();
        dept.setName("Dept " + System.currentTimeMillis());
        dept.setCode("D" + System.currentTimeMillis());
        dept.setDescription("Test Department");
        return departmentRepository.save(dept);
    }
}