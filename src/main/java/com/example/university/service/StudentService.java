package com.example.university.service;

import com.example.university.entity.Course;
import com.example.university.entity.Student;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Student getStudentByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Transactional
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    @Transactional
    public void enrollInCourse(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.enrollInCourse(course);
        studentRepository.save(student);
    }

    @Transactional
    public void unenrollFromCourse(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.unenrollFromCourse(course);
        studentRepository.save(student);
    }

    public List<Course> getEnrolledCourses(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getEnrolledCourses().stream().toList();
    }

    public List<Course> getAvailableCourses(Long studentId) {
        Student student = getStudentById(studentId);
        List<Course> allCourses = courseRepository.findAll();
        List<Course> enrolledCourses = getEnrolledCourses(studentId);

        return allCourses.stream()
                .filter(course -> !enrolledCourses.contains(course))
                .toList();
    }
}