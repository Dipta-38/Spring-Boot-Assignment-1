package com.example.university.service;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Teacher;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.DepartmentRepository;
import com.example.university.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;

    public CourseService(CourseRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Transactional
    public Course createCourse(Course course, Long departmentId, Long teacherId) {
        if (courseRepository.existsByName(course.getName())) {
            throw new RuntimeException("Course name already exists");
        }
        if (courseRepository.existsByCode(course.getCode())) {
            throw new RuntimeException("Course code already exists");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        course.setDepartment(department);
        course.setTeacher(teacher);

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Course courseDetails, Long departmentId, Long teacherId) {
        Course course = getCourseById(id);

        course.setName(courseDetails.getName());
        course.setCode(courseDetails.getCode());
        course.setDescription(courseDetails.getDescription());
        course.setCredits(courseDetails.getCredits());

        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            course.setDepartment(department);
        }

        if (teacherId != null) {
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            course.setTeacher(teacher);
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId);
    }

    public List<Course> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
}